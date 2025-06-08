package io.nasser.mylib.api.host

sealed interface HostFont {
    data object InterFamily : HostFont
    data object InterMedium : HostFont
    data object InterRegular : HostFont
}