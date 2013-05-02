package org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.interfaces;

import java.security.PublicKey;

import org.jenkinsci.plugins.iosbuilder.bouncycastle.math.ec.ECPoint;

/**
 * interface for elliptic curve public keys.
 */
public interface ECPublicKey
    extends ECKey, PublicKey
{
    /**
     * return the public point Q
     */
    public ECPoint getQ();
}
