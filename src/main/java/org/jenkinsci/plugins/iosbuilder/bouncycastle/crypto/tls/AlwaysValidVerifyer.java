package org.jenkinsci.plugins.iosbuilder.bouncycastle.crypto.tls;

/**
 * A certificate verifyer, that will always return true.
 * 
 * <pre>
 * DO NOT USE THIS FILE UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING.
 * </pre>
 * 
 * @deprecated Perform certificate verification in TlsAuthentication implementation
 */
public class AlwaysValidVerifyer implements CertificateVerifyer
{
    /**
     * Return true.
     * 
     * @see CertificateVerifyer#isValid(org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x509.Certificate[])
     */
    public boolean isValid(org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x509.Certificate[] certs)
    {
        return true;
    }
}
