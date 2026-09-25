package com.sr.smart_civic_platform.user.entity;

/*
 * Purpose:
 * User account এর বর্তমান অবস্থা track করা।
 *
 * ACTIVE                 -> normal, login করতে পারবে
 * SUSPENDED               -> admin block করেছে, login করতে পারবে না
 * PENDING_VERIFICATION    -> future email verification feature এর জন্য reserved
 *                            (এখন ব্যবহার হবে না, কিন্তু enum এ রাখা হলো যাতে
 *                             পরে migration করতে না হয়)
 */
public enum UserStatus {
    ACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION
}