SRCREV = "${AUTOREV}"
XEN_REL = "4.14"
LIC_FILES_CHKSUM = "file://COPYING;md5=419739e325a50f3d7b4501338e44a4e5"

# OpenXT's Xen recipes share a common patchqueue so reset SRC_URI
SRC_URI = "git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH}"

require xen-common.inc
require ${@bb.utils.contains('DISTRO_FEATURES', 'blktap2', 'xen-tools-blktap2.inc', 'xen-tools-blktap3.inc', d)}
require xen-tools-openxt.inc

# Workaround for setuptools3 overriding autotools-brokensep
B = "${S}"

DEFAULT_PREFERENCE = "1"

EXTRA_OEMAKE+="-j 1"
EXTRA_OECONF+="--disable-golang"

FILES_${PN}-libxenhypfs = "${libdir}/libxenhypfs.so.*"
FILES_${PN}-libxenhypfs-dev = " \
    ${libdir}/libxenhypfs.so \
    ${libdir}/pkgconfig/xenhypfs.pc \
    ${datadir}/pkgconfig/xenhypfs.pc \
    "
PACKAGES += "\
    ${PN}-libxenhypfs \
    ${PN}-libxenhypfs-dev \
"

RDPENDS_${PN} += "${PN}-libxenhypfs"
