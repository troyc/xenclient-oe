require tboot.inc

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b86c2f3e88dffdbc026c71e2a818c70c"

S = "${WORKDIR}/${PN}-${PV}"

SRC_URI = " \
    https://downloads.sourceforge.net/project/${BPN}/${BPN}/${BPN}-${PV}.tar.gz \
    file://0001-grub2-Adjust-module-placement-locations-when-changin.patch \
    file://0002-tboot-Propagate-failure-to-map_tboot_pages.patch \
    file://0003-tboot-TB_POLTYPE_WARN_ON_FAILURE-with-pre-post.patch \
    file://0004-tboot-Mark-TPM-region-reserved-if-not-already.patch \
    file://0005-pcr-calc-Add-pcr-calculator-tool.patch \
    file://0006-tpm2.0-Perform-orderly-shutdown.patch \
    file://0007-tboot-Export-TPM-event-log-to-VMM-Kernel.patch \
    file://0008-Find-e820-regions-that-include-the-limit.patch \
    file://0009-Add-support-for-launching-64-bit-PE-kernels.patch \
    file://gcc9.patch \
"

SRC_URI[md5sum] = "0922c78db8ca25f610d5cec143319e9a"
SRC_URI[sha256sum] = "3c8c411e672b0d07c42a07435b0f56d8a6e3345801cd3012fa6e8d906dc2923b"

inherit deploy

do_compile() {
    oe_runmake SUBDIRS="tboot" CC="${HOST_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" CPP="${HOST_PREFIX}cpp ${TOOLCHAIN_OPTIONS}"
    if [ "${TBOOT_TARGET_ARCH}" != "x86_32" ]; then
        # Safestringlib is built statically by tboot right before and
        # TBoot is always 32bit (-m32 -march=i686).
        # Clean and rebuild for now.
        oe_runmake SUBDIRS="safestringlib" clean
    fi
    oe_runmake SUBDIRS="safestringlib lcptools lcptools-v2 tb_polgen utils pcr-calc" TARGET_ARCH="${TBOOT_TARGET_ARCH}"
}

do_deploy() {
    install -m 0644 "${D}/boot/tboot.gz" "${DEPLOYDIR}/tboot.gz"
}
addtask do_deploy after do_install before do_build
