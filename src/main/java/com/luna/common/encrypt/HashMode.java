package com.luna.common.encrypt;

import java.util.Arrays;

/**
 * @author luna@mac
 * 2021年03月30日 20:16:00
 */
public enum HashMode {
    MODE_0("MD5", 11095L * 1000 * 1000),
    MODE_1("SHA-256", 11095L * 10000 * 1000),
    MODE_2("SHA-512", 11095L * 10000 * 1000),
    MODE_10("md5($pass.$salt)", 11086L * 1000 * 1000),
    MODE_11("Joomla < 2.5.18", 11032L * 1000 * 1000),
    MODE_12("PostgreSQL", 11033L * 1000 * 1000),
    MODE_20("md5($salt.$pass)", 5884L * 1000 * 1000),
    MODE_21("osCommerce, xt:Commerce", 5868L * 1000 * 1000),
    MODE_22("Juniper NetScreen/SSG (ScreenOS)", 6006L * 1000 * 1000),
    MODE_23("Skype", 5928L * 1000 * 1000),
    MODE_30("md5(utf16le($pass).$salt)", 11065L * 1000 * 1000),
    MODE_40("md5($salt.utf16le($pass))", 5874L * 1000 * 1000),
    MODE_50("HMAC-MD5 (key = $pass)", 1648L * 1000 * 1000),
    MODE_60("HMAC-MD5 (key = $salt)", 3371L * 1000 * 1000),
    MODE_100("SHA1", 3743L * 1000 * 1000),
    MODE_101("nsldap, SHA-1(Base64), Netscape LDAP SHA", 3855L * 1000 * 1000),
    MODE_110("sha1($pass.$salt)", 3858L * 1000 * 1000),
    MODE_111("nsldaps, SSHA-1(Base64), Netscape LDAP SSHA", 3756L * 1000 * 1000),
    MODE_112("Oracle S: Type (Oracle 11+)", 3822L * 1000 * 1000),
    MODE_120("sha1($salt.$pass)", 2928L * 1000 * 1000),
    MODE_121("SMF (Simple Machines Forum) > v1.1", 2933L * 1000 * 1000),
    MODE_122("macOS v10.4, macOS v10.5, MacOS v10.6", 2924L * 1000 * 1000),
    MODE_124("Django (SHA-1)", 2933L * 1000 * 1000),
    MODE_125("ArubaOS", 2919L * 1000 * 1000),
    MODE_130("sha1(utf16le($pass).$salt)", 3863L * 1000 * 1000),
    MODE_131("MSSQL (2000)", 3856L * 1000 * 1000),
    MODE_132("MSSQL (2005)", 3854L * 1000 * 1000),
    MODE_133("PeopleSoft", 3859L * 1000 * 1000),
    MODE_140("sha1($salt.utf16le($pass))", 2938L * 1000 * 1000),
    MODE_141("Episerver 6.x < .NET 4", 2937L * 1000 * 1000),
    MODE_150("HMAC-SHA1 (key = $pass)", 868L * 1000 * 1000),
    MODE_160("HMAC-SHA1 (key = $salt)", 1615L * 1000 * 1000),
    MODE_200("MySQL323", 28884L * 1000 * 1000),
    MODE_300("MySQL4.1/MySQL5", 1686L * 1000 * 1000),
    MODE_400("phpass (Iterations: 2048)", 3218L * 1000),
    MODE_500("md5crypt, MD5 (Unix), Cisco-IOS $1$ (MD5) (Iterations: 1000)", 4381L * 1000),
    MODE_501("Juniper IVE (Iterations: 1000)", 4379L * 1000),
    MODE_600("BLAKE2b-512", 1187L * 1000 * 1000),
    MODE_900("MD4", 21266L * 1000 * 1000),
    MODE_1000("NTLM", 21317L * 1000 * 1000),
    MODE_1100("Domain Cached Credentials (DCC), MS Cache", 5897L * 1000 * 1000),
    MODE_1300("SHA2-224", 1636L * 1000 * 1000),
    MODE_1400("SHA2-256", 1665L * 1000 * 1000),
    MODE_1410("sha256($pass.$salt)", 1653L * 1000 * 1000),
    MODE_1411("SSHA-256(Base64), LDAP {SSHA256}", 1658L * 1000 * 1000),
    MODE_1420("sha256($salt.$pass)", 1489L * 1000 * 1000),
    MODE_1421("hMailServer", 1479L * 1000 * 1000),
    MODE_1430("sha256(utf16le($pass).$salt)", 1626L * 1000 * 1000),
    MODE_1440("sha256($salt.utf16le($pass))", 1481L * 1000 * 1000),
    MODE_1441("Episerver 6.x >= .NET 4", 1485L * 1000 * 1000),
    MODE_1450("HMAC-SHA256 (key = $pass)", 327L * 1000 * 1000),
    MODE_1460("HMAC-SHA256 (key = $salt)", 713L * 1000 * 1000),
    MODE_1500("descrypt, DES (Unix), Traditional DES", 409L * 1000 * 1000),
    MODE_1600("Apache $apr1$ MD5, md5apr1, MD5 (APR) (Iterations: 1000)", 4370L * 1000),
    MODE_1700("SHA2-512", 502L * 1000 * 1000),
    MODE_1710("sha512($pass.$salt)", 439L * 1000 * 1000),
    MODE_1711("SSHA-512(Base64), LDAP {SSHA512}", 444L * 1000 * 1000),
    MODE_1720("sha512($salt.$pass)", 459L * 1000 * 1000),
    MODE_1722("macOS v10.7", 473L * 1000 * 1000),
    MODE_1730("sha512(utf16le($pass).$salt)", 439L * 1000 * 1000),
    MODE_1731("MSSQL (2012, 2014)", 442L * 1000 * 1000),
    MODE_1740("sha512($salt.utf16le($pass))", 440L * 1000 * 1000),
    MODE_1750("HMAC-SHA512 (key = $pass)", 93165L * 1000),
    MODE_1760("HMAC-SHA512 (key = $salt)", 198L * 1000 * 1000),
    MODE_1800("sha512crypt $6$, SHA512 (Unix) (Iterations: 5000)", 69961L),
    MODE_2000("STDOUT", 11932L * 1000 * 1000 * 1000),
    MODE_2100("Domain Cached Credentials 2 (DCC2), MS Cache 2 (Iterations: 10239)", 150L * 1000),
    MODE_2400("Cisco-PIX MD5", 7323L * 1000 * 1000),
    MODE_2410("Cisco-ASA MD5", 7388L * 1000 * 1000),
    MODE_2500("WPA-EAPOL-PBKDF2 (Iterations: 4095)", 189L * 1000),
    MODE_2501("WPA-EAPOL-PMK (Iterations: 0)", 151L * 1000 * 1000),
    MODE_2600("md5(md5($pass))", 3282L * 1000 * 1000),
    MODE_2611("vBulletin < v3.8.5", 3277L * 1000 * 1000),
    MODE_2612("PHPS", 3276L * 1000 * 1000),
    MODE_2711("vBulletin >= v3.8.5", 2215L * 1000 * 1000),
    MODE_2811("MyBB 1.2+, IPB2+ (Invision Power Board)", 2299L * 1000 * 1000),
    MODE_3000("LM", 9576L * 1000 * 1000),
    MODE_3100("Oracle H: Type (Oracle 7+)", 407L * 1000 * 1000),
    MODE_3200("bcrypt $2*$, Blowfish (Unix) (Iterations: 32)", 9651L),
    MODE_3710("md5($salt.md5($pass))", 3051L * 1000 * 1000),
    MODE_3711("MediaWiki B type", 3033L * 1000 * 1000),
    MODE_3800("md5($salt.$pass.$salt)", 5853L * 1000 * 1000),
    MODE_3910("md5(md5($pass).md5($salt))", 2216L * 1000 * 1000),
    MODE_4010("md5($salt.md5($salt.$pass))", 2723L * 1000 * 1000),
    MODE_4110("md5($salt.md5($pass.$salt))", 3004L * 1000 * 1000),
    MODE_4300("md5(strtoupper(md5($pass)))", 3280L * 1000 * 1000),
    MODE_4400("md5(sha1($pass))", 2046L * 1000 * 1000),
    MODE_4500("sha1(sha1($pass))", 1517L * 1000 * 1000),
    MODE_4520("sha1($salt.sha1($pass))", 926L * 1000 * 1000),
    MODE_4521("Redmine", 931L * 1000 * 1000),
    MODE_4522("PunBB", 1409L * 1000 * 1000),
    MODE_4700("sha1(md5($pass))", 2081L * 1000 * 1000),
    MODE_4710("sha1(md5($pass).$salt)", 1971L * 1000 * 1000),
    MODE_4711("Huawei sha1(md5($pass).$salt)", 1971L * 1000 * 1000),
    MODE_4800("iSCSI CHAP authentication, MD5(CHAP)", 7282L * 1000 * 1000),
    MODE_4900("sha1($salt.$pass.$salt)", 2878L * 1000 * 1000),
    MODE_5100("Half MD5", 6703L * 1000 * 1000),
    MODE_5200("Password Safe v3 (Iterations: 2049)", 636L * 1000),
    MODE_5300("IKE-PSK MD5", 352L * 1000 * 1000),
    MODE_5400("IKE-PSK SHA1", 167L * 1000 * 1000),
    MODE_5500("NetNTLMv1 / NetNTLMv1+ESS", 12358L * 1000 * 1000),
    MODE_5600("NetNTLMv2", 736L * 1000 * 1000),
    MODE_5700("Cisco-IOS type 4 (SHA256)", 1671L * 1000 * 1000),
    MODE_5800("Samsung Android Password/PIN (Iterations: 1023)", 2642L * 1000),
    MODE_6000("RIPEMD-160", 2287L * 1000 * 1000),
    MODE_6100("Whirlpool", 249L * 1000 * 1000),
    MODE_6211("TrueCrypt RIPEMD160 + XTS 512 bit (Iterations: 1999)", 138L * 1000),
    MODE_6212("TrueCrypt RIPEMD160 + XTS 1024 bit (Iterations: 1999)", 78978L),
    MODE_6213("TrueCrypt RIPEMD160 + XTS 1536 bit (Iterations: 1999)", 55393L),
    MODE_6221("TrueCrypt SHA512 + XTS 512 bit (Iterations: 999)", 202L * 1000),
    MODE_6222("TrueCrypt SHA512 + XTS 1024 bit (Iterations: 999)", 100L * 1000),
    MODE_6223("TrueCrypt SHA512 + XTS 1536 bit (Iterations: 999)", 66970L),
    MODE_6231("TrueCrypt Whirlpool + XTS 512 bit (Iterations: 999)", 19864L),
    MODE_6232("TrueCrypt Whirlpool + XTS 1024 bit (Iterations: 999)", 9011L),
    MODE_6233("TrueCrypt Whirlpool + XTS 1536 bit (Iterations: 999)", 6734L),
    MODE_6241("TrueCrypt RIPEMD160 + XTS 512 bit + boot-mode (Iterations: 999)", 274L * 1000),
    MODE_6242("TrueCrypt RIPEMD160 + XTS 1024 bit + boot-mode (Iterations: 999)", 157L * 1000),
    MODE_6243("TrueCrypt RIPEMD160 + XTS 1536 bit + boot-mode (Iterations: 999)", 107L * 1000),
    MODE_6300("AIX {smd5} (Iterations: 1000)", 4383L * 1000),
    MODE_6400("AIX {ssha256} (Iterations: 63)", 9821L * 1000),
    MODE_6500("AIX {ssha512} (Iterations: 63)", 2797L * 1000),
    MODE_6600("1Password, agilekeychain (Iterations: 999)", 1546L * 1000),
    MODE_6700("AIX {ssha1} (Iterations: 63)", 21642L * 1000),
    MODE_6800("LastPass + LastPass sniffed (Iterations: 499)", 1396L * 1000),
    MODE_6900("GOST R 34.11-94", 264L * 1000 * 1000),
    MODE_7000("FortiGate (FortiOS)", 3362L * 1000 * 1000),
    MODE_7100("macOS v10.8+ (PBKDF2-SHA512) (Iterations: 1023)", 199L * 1000),
    MODE_7200("GRUB 2 (Iterations: 1023)", 191L * 1000),
    MODE_7300("IPMI2 RAKP HMAC-SHA1", 500L * 1000 * 1000),
    MODE_7400("sha256crypt $5$, SHA256 (Unix) (Iterations: 5000)", 147L * 1000),
    MODE_7401("MySQL $A$ (sha256crypt) (Iterations: 5000)", 139L * 1000),
    MODE_7500("Kerberos 5, etype 23, AS-REQ Pre-Auth", 127L * 1000 * 1000),
    MODE_7700("SAP CODVN B (BCODE)", 486L * 1000 * 1000),
    MODE_7701("SAP CODVN B (BCODE) from RFC_READ_TABLE", 485L * 1000 * 1000),
    MODE_7800("SAP CODVN F/G (PASSCODE)", 468L * 1000 * 1000),
    MODE_7801("SAP CODVN F/G (PASSCODE) from RFC_READ_TABLE", 471L * 1000 * 1000),
    MODE_7900("Drupal7 (Iterations: 16384)", 25487L),
    MODE_8000("Sybase ASE", 210L * 1000 * 1000),
    MODE_8100("Citrix NetScaler (SHA1)", 3246L * 1000 * 1000),
    MODE_8200("1Password, cloudkeychain (Iterations: 39999)", 5123L),
    MODE_8300("DNSSEC (NSEC3)", 1431L * 1000 * 1000),
    MODE_8400("WBB3 (Woltlab Burning Board)", 631L * 1000 * 1000),
    MODE_8500("RACF", 2022L * 1000 * 1000),
    MODE_8600("Lotus Notes/Domino 5", 231L * 1000 * 1000),
    MODE_8700("Lotus Notes/Domino 6", 56609L * 1000),
    MODE_8800("Android FDE <= 4.3 (Iterations: 1999)", 390L * 1000),
    MODE_8900("scrypt (Iterations: 1)", 179L * 1000),
    MODE_9000("Password Safe v2 (Iterations: 1000)", 207L * 1000),
    MODE_9100("Lotus Notes/Domino 8 (Iterations: 4999)", 309L * 1000),
    MODE_9200("Cisco-IOS $8$ (PBKDF2-SHA256) (Iterations: 19999)", 33217L),
    MODE_9300("Cisco-IOS $9$ (scrypt) (Iterations: 1)", 1682L),
    MODE_9400("MS Office 2007 (Iterations: 50000)", 62977L),
    MODE_9500("MS Office 2010 (Iterations: 100000)", 31427L),
    MODE_9600("MS Office 2013 (Iterations: 100000)", 4615L),
    MODE_9700("MS Office <= 2003 $0/$1, MD5 + RC4", 124L * 1000 * 1000),
    MODE_9710("MS Office <= 2003 $0/$1, MD5 + RC4, collider #1", 158L * 1000 * 1000),
    MODE_9720("MS Office <= 2003 $0/$1, MD5 + RC4, collider #2", 896L * 1000 * 1000),
    MODE_9800("MS Office <= 2003 $3/$4, SHA1 + RC4", 135L * 1000 * 1000),
    MODE_9810("MS Office <= 2003 $3, SHA1 + RC4, collider #1", 156L * 1000 * 1000),
    MODE_9820("MS Office <= 2003 $3, SHA1 + RC4, collider #2", 1043L * 1000 * 1000),
    MODE_9900("Radmin2", 3600L * 1000 * 1000),
    MODE_10000("Django (PBKDF2-SHA256) (Iterations: 9999)", 66276L),
    MODE_10100("SipHash", 17062L * 1000 * 1000),
    MODE_10200("CRAM-MD5", 1700L * 1000 * 1000),
    MODE_10300("SAP CODVN H (PWDSALTEDHASH) iSSHA-1 (Iterations: 1023)", 2326L * 1000),
    MODE_10400("PDF 1.1 - 1.3 (Acrobat 2 - 4)", 166L * 1000 * 1000),
    MODE_10410("PDF 1.1 - 1.3 (Acrobat 2 - 4), collider #1", 184L * 1000 * 1000),
    MODE_10420("PDF 1.1 - 1.3 (Acrobat 2 - 4), collider #2", 3425L * 1000 * 1000),
    MODE_10500("PDF 1.4 - 1.6 (Acrobat 5 - 8) (Iterations: 70)", 7970L * 1000),
    MODE_10600("PDF 1.7 Level 3 (Acrobat 9)", 1675L * 1000 * 1000),
    MODE_10700("PDF 1.7 Level 8 (Acrobat 10 - 11) (Iterations: 64)", 17334L),
    MODE_10800("SHA2-384", 497L * 1000 * 1000),
    MODE_10900("PBKDF2-HMAC-SHA256 (Iterations: 999)", 645L * 1000),
    MODE_10901("RedHat 389-DS LDAP (PBKDF2-HMAC-SHA256) (Iterations: 8191)", 80426L),
    MODE_11000("PrestaShop", 3727L * 1000 * 1000),
    MODE_11100("PostgreSQL CRAM (MD5)", 3235L * 1000 * 1000),
    MODE_11200("MySQL CRAM (SHA1)", 951L * 1000 * 1000),
    MODE_11300("Bitcoin/Litecoin wallet.dat (Iterations: 200459)", 2259L),
    MODE_11400("SIP digest authentication (MD5)", 1496L * 1000 * 1000),
    MODE_11500("CRC32", 20089L * 1000 * 1000),
    MODE_11600("7-Zip (Iterations: 16384)", 200L * 1000),
    MODE_11700("GOST R 34.11-2012 (Streebog) 256-bit, big-endian", 33147L * 1000),
    MODE_11750("HMAC-Streebog-256 (key = $pass), big-endian", 8713L * 1000),
    MODE_11760("HMAC-Streebog-256 (key = $salt), big-endian", 18646L * 1000),
    MODE_11800("GOST R 34.11-2012 (Streebog) 512-bit, big-endian", 29288L * 1000),
    MODE_11850("HMAC-Streebog-512 (key = $pass), big-endian", 8149L * 1000),
    MODE_11860("HMAC-Streebog-512 (key = $salt), big-endian", 15184L * 1000),
    MODE_11900("PBKDF2-HMAC-MD5 (Iterations: 999)", 3266L * 1000),
    MODE_12000("PBKDF2-HMAC-SHA1 (Iterations: 999)", 1399L * 1000),
    MODE_12001("Atlassian (PBKDF2-HMAC-SHA1) (Iterations: 9999)", 149L * 1000),
    MODE_12100("PBKDF2-HMAC-SHA512 (Iterations: 999)", 201L * 1000),
    MODE_12200("eCryptfs (Iterations: 65536)", 6942L),
    MODE_12300("Oracle T: Type (Oracle 12+) (Iterations: 4095)", 50000L),
    MODE_12400("BSDi Crypt, Extended DES (Iterations: 2194)", 1915L * 1000),
    MODE_12500("RAR3-hp (Iterations: 262144)", 19459L),
    MODE_12600("ColdFusion 10+", 927L * 1000 * 1000),
    MODE_12700("Blockchain, My Wallet (Iterations: 9)", 43840L * 1000),
    MODE_12800("MS-AzureSync PBKDF2-HMAC-SHA256 (Iterations: 99)", 5504L * 1000),
    MODE_12900("Android FDE (Samsung DEK) (Iterations: 4095)", 171L * 1000),
    MODE_13000("RAR5 (Iterations: 32799)", 21212L),
    MODE_13100("Kerberos 5, etype 23, TGS-REP", 85456L * 1000),
    MODE_13200("AxCrypt (Iterations: 10467)", 122L * 1000),
    MODE_13300("AxCrypt in-memory SHA1", 3677L * 1000 * 1000),
    MODE_13400("KeePass 1 (AES/Twofish) and KeePass 2 (AES) (Iterations: 24569)", 37571L),
    MODE_13500("PeopleSoft PS_TOKEN", 2757L * 1000 * 1000),
    MODE_13600("WinZip (Iterations: 999)", 1420L * 1000),
    MODE_13711("VeraCrypt RIPEMD160 + XTS 512 bit (Iterations: 655330)", 438L),
    MODE_13712("VeraCrypt RIPEMD160 + XTS 1024 bit (Iterations: 655330)", 252L),
    MODE_13713("VeraCrypt RIPEMD160 + XTS 1536 bit (Iterations: 655330)", 159L),
    MODE_13721("VeraCrypt SHA512 + XTS 512 bit (Iterations: 499999)", 417L),
    MODE_13722("VeraCrypt SHA512 + XTS 1024 bit (Iterations: 499999)", 209L),
    MODE_13723("VeraCrypt SHA512 + XTS 1536 bit (Iterations: 499999)", 139L),
    MODE_13731("VeraCrypt Whirlpool + XTS 512 bit (Iterations: 499999)", 35L),
    MODE_13732("VeraCrypt Whirlpool + XTS 1024 bit (Iterations: 499999)", 18L),
    MODE_13733("VeraCrypt Whirlpool + XTS 1536 bit (Iterations: 499999)", 13L),
    MODE_13741("VeraCrypt RIPEMD160 + XTS 512 bit + boot-mode (Iterations: 327660)", 852L),
    MODE_13742("VeraCrypt RIPEMD160 + XTS 1024 bit + boot-mode (Iterations: 327660)", 482L),
    MODE_13743("VeraCrypt RIPEMD160 + XTS 1536 bit + boot-mode (Iterations: 327660)", 341L),
    MODE_13751("VeraCrypt SHA256 + XTS 512 bit (Iterations: 499999)", 654L),
    MODE_13752("VeraCrypt SHA256 + XTS 1024 bit (Iterations: 499999)", 326L),
    MODE_13753("VeraCrypt SHA256 + XTS 1536 bit (Iterations: 499999)", 217L),
    MODE_13761("VeraCrypt SHA256 + XTS 512 bit + boot-mode (Iterations: 199999)", 1628L),
    MODE_13762("VeraCrypt SHA256 + XTS 1024 bit + boot-mode (Iterations: 199999)", 819L),
    MODE_13763("VeraCrypt SHA256 + XTS 1536 bit + boot-mode (Iterations: 199999)", 542L),
    MODE_13771("VeraCrypt Streebog-512 + XTS 512 bit (Iterations: 499999)", 20L),
    MODE_13772("VeraCrypt Streebog-512 + XTS 1024 bit (Iterations: 499999)", 9L),
    MODE_13773("VeraCrypt Streebog-512 + XTS 1536 bit (Iterations: 499999)", 6L),
    MODE_13800("Windows Phone 8+ PIN/password", 399L * 1000 * 1000),
    MODE_13900("OpenCart", 961L * 1000 * 1000),
    MODE_14000("DES (PT = $salt, key = $pass)", 10172L * 1000 * 1000),
    MODE_14100("3DES (PT = $salt, key = $pass)", 2020L * 1000 * 1000),
    MODE_14400("sha1(CX)", 176L * 1000 * 1000),
    MODE_14600("LUKS (Iterations: 163044)", 4632L),
    MODE_14700("iTunes backup < 10.0 (Iterations: 9999)", 77288L),
    MODE_14800("iTunes backup >= 10.0 (Iterations: 9999999)", 66L),
    MODE_14900("Skip32 (PT = $salt, key = $pass)", 5457L * 1000 * 1000),
    MODE_15000("FileZilla Server >= 0.9.55", 486L * 1000 * 1000),
    MODE_15100("Juniper/NetBSD sha1crypt (Iterations: 19999)", 78006L),
    MODE_15200("Blockchain, My Wallet, V2 (Iterations: 4999)", 151L * 1000),
    MODE_15300("DPAPI masterkey file v1 (Iterations: 23999)", 31265L),
    MODE_15400("ChaCha20", 2517L * 1000 * 1000),
    MODE_15500("JKS Java Key Store Private Keys (SHA1)", 3615L * 1000 * 1000),
    MODE_15600("Ethereum Wallet, PBKDF2-HMAC-SHA256 (Iterations: 1023)", 642L * 1000),
    MODE_15700("Ethereum Wallet, SCRYPT (Iterations: 1)", 0L),
    MODE_15900("DPAPI masterkey file v2 (Iterations: 12899)", 15750L),
    MODE_16000("Tripcode", 157L * 1000 * 1000),
    MODE_16100("TACACS+", 6662L * 1000 * 1000),
    MODE_16200("Apple Secure Notes (Iterations: 19999)", 32888L),
    MODE_16300("Ethereum Pre-Sale Wallet, PBKDF2-HMAC-SHA256 (Iterations: 1999)", 316L * 1000),
    MODE_16400("CRAM-MD5 Dovecot", 10868L * 1000 * 1000),
    MODE_16500("JWT (JSON Web Token)", 311L * 1000 * 1000),
    MODE_16600("Electrum Wallet (Salt-Type 1-3)", 267L * 1000 * 1000),
    MODE_16700("FileVault 2 (Iterations: 19999)", 32754L),
    MODE_16800("WPA-PMKID-PBKDF2 (Iterations: 4095)", 188L * 1000),
    MODE_16801("WPA-PMKID-PMK (Iterations: 0)", 148L * 1000 * 1000),
    MODE_16900("Ansible Vault (Iterations: 9999)", 67622L),
    MODE_17200("PKZIP (Compressed)", 0L),
    MODE_17210("PKZIP (Uncompressed)", 463L * 1000 * 1000),
    MODE_17220("PKZIP (Compressed Multi-File)", 0L),
    MODE_17225("PKZIP (Mixed Multi-File)", 0L),
    MODE_17230("PKZIP (Mixed Multi-File Checksum-Only)", 4791L * 1000 * 1000),
    MODE_17300("SHA3-224", 344L * 1000 * 1000),
    MODE_17400("SHA3-256", 345L * 1000 * 1000),
    MODE_17500("SHA3-384", 341L * 1000 * 1000),
    MODE_17600("SHA3-512", 343L * 1000 * 1000),
    MODE_17700("Keccak-224", 336L * 1000 * 1000),
    MODE_17800("Keccak-256", 336L * 1000 * 1000),
    MODE_17900("Keccak-384", 341L * 1000 * 1000),
    MODE_18000("Keccak-512", 341L * 1000 * 1000),
    MODE_18100("TOTP (HMAC-SHA1)", 764L * 1000 * 1000),
    MODE_18200("Kerberos 5, etype 23, AS-REP", 85001L * 1000),
    MODE_18300("Apple File System (APFS) (Iterations: 19999)", 33633L),
    MODE_18400("Open Document Format (ODF) 1.2 (SHA-256, AES) (Iterations: 99999)", 7654L),
    MODE_18500("sha1(md5(md5($pass)))", 1408L * 1000 * 1000),
    MODE_18600("Open Document Format (ODF) 1.1 (SHA-1, Blowfish) (Iterations: 1023)", 221L * 1000),
    MODE_18700("Java Object hashCode()", 41015L * 1000 * 1000),
    MODE_18800("Blockchain, My Wallet, Second Password (SHA256) (Iterations: 9999)", 133L * 1000),
    MODE_18900("Android Backup (Iterations: 9999)", 78367L),
    MODE_19000("QNX /etc/shadow (MD5) (Iterations: 1000)", 2182L * 1000),
    MODE_19100("QNX /etc/shadow (SHA256) (Iterations: 1000)", 2428L * 1000),
    MODE_19200("QNX /etc/shadow (SHA512) (Iterations: 1000)", 1179L * 1000),
    MODE_19300("sha1($salt1.$pass.$salt2)", 236L * 1000 * 1000),
    MODE_19500("Ruby on Rails Restful-Authentication", 57007L * 1000),
    MODE_19600("Kerberos 5, etype 17, TGS-REP (Iterations: 4095)", 379L * 1000),
    MODE_19700("Kerberos 5, etype 18, TGS-REP (Iterations: 4095)", 188L * 1000),
    MODE_19800("Kerberos 5, etype 17, Pre-Auth (Iterations: 4095)", 379L * 1000),
    MODE_19900("Kerberos 5, etype 18, Pre-Auth (Iterations: 4095)", 190L * 1000),
    MODE_20011("DiskCryptor SHA512 + XTS 512 bit (Iterations: 999)", 202L * 1000),
    MODE_20012("DiskCryptor SHA512 + XTS 1024 bit (Iterations: 999)", 101L * 1000),
    MODE_20013("DiskCryptor SHA512 + XTS 1536 bit (Iterations: 999)", 67755L),
    MODE_20200("Python passlib pbkdf2-sha512 (Iterations: 24999)", 8250L),
    MODE_20300("Python passlib pbkdf2-sha256 (Iterations: 28999)", 22929L),
    MODE_20400("Python passlib pbkdf2-sha1 (Iterations: 130999)", 11713L),
    MODE_20500("PKZIP Master Key", 51077L * 1000 * 1000),
    MODE_20510("PKZIP Master Key (6 byte optimization)", 5446L * 1000 * 1000),
    MODE_20600("Oracle Transportation Management (SHA256) (Iterations: 999)", 1359L * 1000),
    MODE_20710("sha256(sha256($pass).$salt)", 445L * 1000 * 1000),
    MODE_20711("AuthMe sha256", 438L * 1000 * 1000),
    MODE_20800("sha256(md5($pass))", 1249L * 1000 * 1000),
    MODE_20900("md5(sha1($pass).md5($pass).sha1($pass))", 1155L * 1000 * 1000),
    MODE_21000("BitShares v0.x – sha512(sha512_bin(pass))", 232L * 1000 * 1000),
    MODE_21100("sha1(md5($pass.$salt))", 2096L * 1000 * 1000),
    MODE_21200("md5(sha1($salt).md5($pass))", 2057L * 1000 * 1000),
    MODE_21300("md5($salt.sha1($salt.$pass))", 1339L * 1000 * 1000),
    MODE_21400("sha256(sha256_bin($pass))", 742L * 1000 * 1000),
    MODE_21500("SolarWinds Orion (Iterations: 999)", 30186L),
    MODE_21600("Web2py pbkdf2-sha512 (Iterations: 999)", 204L * 1000),
    MODE_21700("Electrum Wallet (Salt-Type 4) (Iterations: 1023)", 0L),
    MODE_21800("Electrum Wallet (Salt-Type 5) (Iterations: 1023)", 0L),
    MODE_22000("WPA-PBKDF2-PMKID+EAPOL (Iterations: 4095)", 190L * 1000),
    MODE_22001("WPA-PMK-PMKID+EAPOL (Iterations: 0)", 150L * 1000 * 1000),
    MODE_22100("BitLocker (Iterations: 1048576)", 724L),
    MODE_22200("Citrix NetScaler (SHA512)", 495L * 1000 * 1000),
    MODE_22300("sha256($salt.$pass.$salt)", 1460L * 1000 * 1000),
    MODE_22301("Telegram Mobile App Passcode (SHA256)", 1418L * 1000 * 1000),
    MODE_22400("AES Crypt (SHA256) (Iterations: 8191)", 145L * 1000),
    MODE_22500("MultiBit Classic .key (MD5)", 301L * 1000 * 1000),
    MODE_22600("Telegram Desktop App Passcode (PBKDF2-HMAC-SHA1) (Iterations: 3999)", 55139L),
    MODE_22700("MultiBit HD (scrypt) (Iterations: 1)", 26L),
    MODE_23001("SecureZIP AES-128", 427L * 1000 * 1000),
    MODE_23002("SecureZIP AES-192", 300L * 1000 * 1000),
    MODE_23003("SecureZIP AES-256", 203L * 1000 * 1000),
    MODE_99999("Plaintext", 21476L * 1000 * 1000),
    ;

    private String name;
    /** h/s, use AMD RX 570 as standard */
    private Long   speed;

    HashMode(String name, Long speed) {
        this.name = name;
        this.speed = speed;
    }

    public static HashMode get(Integer type) {
        return Arrays.stream(HashMode.values()).filter(hashMode -> hashMode.name().equals("MODE_" + type)).findFirst()
            .orElse(null);
    }

    public Integer getMode() {
        return Integer.parseInt(this.name().substring(5));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    /**
     * 即5分钟的计算量
     *
     * @return
     */
    public Long getLinesPerTask() {
        return speed * 60 * 5;
    }
}
