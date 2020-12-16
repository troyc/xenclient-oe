require tboot.inc

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=7730ab1e15a162ca347bcc1722486d89"

S = "${WORKDIR}/${PN}-${PV}"

SRC_URI = " \
    https://downloads.sourceforge.net/project/${BPN}/${BPN}/${BPN}-${PV}.tar.gz \
    file://0001-config-Allow-build-system-integration.patch \
    file://0002-grub2-Adjust-module-placement-locations-when-changin.patch \
    file://0003-tboot-Propagate-failure-to-map_tboot_pages.patch \
    file://0004-tboot-TB_POLTYPE_WARN_ON_FAILURE-with-pre-post.patch \
    file://0005-tboot-Mark-TPM-region-reserved-if-not-already.patch \
    file://0006-pcr-calc-Add-pcr-calculator-tool.patch \
    file://0007-tboot-Use-SHA256-by-default-with-TPM2.0.patch \
    file://0008-tpm2.0-Perform-orderly-shutdown.patch \
    file://0009-tboot-Export-TPM-event-log-to-VMM-Kernel.patch \
    file://0010-Find-e820-regions-that-include-the-limit.patch \
    file://0011-Add-support-for-launching-64-bit-PE-kernels.patch \
    file://0012-safestringlib-Attend-GCC-warnings.patch \
    file://gcc9.patch \
"

SRC_URI[md5sum] = "5454cae3bf4c4ba47e7dac14ea3088b4"
SRC_URI[sha256sum] = "d6f2334ae41b90e8403a2cd91a819d54356edcb81af2924f47774c72e48d3a9e"

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
