package com.sr.smart_civic_platform.user.entity;

/*
 * Purpose:
 * System এ কোন ধরনের user আছে তা define করা।
 *
 * Why Enum, not String:
 * Typo বা invalid value compile time এ ধরা পড়বে, runtime এ না।
 *
 * CITIZEN  -> complaint submit করে
 * STAFF    -> complaint কে respond/resolve করে
 * ADMIN    -> পুরো system manage করে
 */
public enum UserRole {
    CITIZEN,
    STAFF,
    ADMIN
}