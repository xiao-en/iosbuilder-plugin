package org.jenkinsci.plugins.iosbuilder.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;

import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ASN1Encodable;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ASN1Encoding;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ASN1Primitive;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.DERBitString;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.DERInteger;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.DERNull;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.DERObjectIdentifier;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x9.X962Parameters;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x9.X9ECParameters;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.crypto.params.ECDomainParameters;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jcajce.provider.asymmetric.ec.EC5Util;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jcajce.provider.asymmetric.ec.ECUtil;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.interfaces.ECPointEncoder;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.jenkinsci.plugins.iosbuilder.bouncycastle.math.ec.ECCurve;

public class BCDSTU4145PrivateKey
    implements ECPrivateKey, org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    static final long serialVersionUID = 7245981689601667138L;

    private String algorithm = "DSTU4145";
    private boolean withCompression;

    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCDSTU4145PrivateKey()
    {
    }

    public BCDSTU4145PrivateKey(
        ECPrivateKey key)
    {
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
    }

    public BCDSTU4145PrivateKey(
        org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.spec.ECPrivateKeySpec spec)
    {
        this.d = spec.getD();

        if (spec.getParams() != null) // can be null if implicitlyCA
        {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve;

            ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());

            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        }
        else
        {
            this.ecSpec = null;
        }
    }


    public BCDSTU4145PrivateKey(
        ECPrivateKeySpec spec)
    {
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
    }

    public BCDSTU4145PrivateKey(
        BCDSTU4145PrivateKey key)
    {
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
    }

    public BCDSTU4145PrivateKey(
        String algorithm,
        ECPrivateKeyParameters params,
        BCDSTU4145PublicKey pubKey,
        ECParameterSpec spec)
    {
        ECDomainParameters dp = params.getParameters();

        this.algorithm = algorithm;
        this.d = params.getD();

        if (spec == null)
        {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());

            this.ecSpec = new ECParameterSpec(
                ellipticCurve,
                new ECPoint(
                    dp.getG().getX().toBigInteger(),
                    dp.getG().getY().toBigInteger()),
                dp.getN(),
                dp.getH().intValue());
        }
        else
        {
            this.ecSpec = spec;
        }

        publicKey = getPublicKeyDetails(pubKey);
    }

    public BCDSTU4145PrivateKey(
        String algorithm,
        ECPrivateKeyParameters params,
        BCDSTU4145PublicKey pubKey,
        org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.spec.ECParameterSpec spec)
    {
        ECDomainParameters dp = params.getParameters();

        this.algorithm = algorithm;
        this.d = params.getD();

        if (spec == null)
        {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());

            this.ecSpec = new ECParameterSpec(
                ellipticCurve,
                new ECPoint(
                    dp.getG().getX().toBigInteger(),
                    dp.getG().getY().toBigInteger()),
                dp.getN(),
                dp.getH().intValue());
        }
        else
        {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());

            this.ecSpec = new ECParameterSpec(
                ellipticCurve,
                new ECPoint(
                    spec.getG().getX().toBigInteger(),
                    spec.getG().getY().toBigInteger()),
                spec.getN(),
                spec.getH().intValue());
        }

        publicKey = getPublicKeyDetails(pubKey);
    }

    public BCDSTU4145PrivateKey(
        String algorithm,
        ECPrivateKeyParameters params)
    {
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
    }

    BCDSTU4145PrivateKey(
        PrivateKeyInfo info)
        throws IOException
    {
        populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info)
        throws IOException
    {
        X962Parameters params = new X962Parameters((ASN1Primitive)info.getPrivateKeyAlgorithm().getParameters());

        if (params.isNamedCurve())
        {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);

            if (ecP == null) // DSTU Curve
            {
                ECDomainParameters gParam = DSTU4145NamedCurves.getByOID(oid);
                EllipticCurve ellipticCurve = EC5Util.convertCurve(gParam.getCurve(), gParam.getSeed());

                ecSpec = new ECNamedCurveSpec(
                    oid.getId(),
                    ellipticCurve,
                    new ECPoint(
                        gParam.getG().getX().toBigInteger(),
                        gParam.getG().getY().toBigInteger()),
                    gParam.getN(),
                    gParam.getH());
            }
            else
            {
                EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());

                ecSpec = new ECNamedCurveSpec(
                    ECUtil.getCurveName(oid),
                    ellipticCurve,
                    new ECPoint(
                        ecP.getG().getX().toBigInteger(),
                        ecP.getG().getY().toBigInteger()),
                    ecP.getN(),
                    ecP.getH());
            }
        }
        else if (params.isImplicitlyCA())
        {
            ecSpec = null;
        }
        else
        {
            X9ECParameters ecP = X9ECParameters.getInstance(params.getParameters());
            EllipticCurve ellipticCurve = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());

            this.ecSpec = new ECParameterSpec(
                ellipticCurve,
                new ECPoint(
                    ecP.getG().getX().toBigInteger(),
                    ecP.getG().getY().toBigInteger()),
                ecP.getN(),
                ecP.getH().intValue());
        }

        ASN1Encodable privKey = info.parsePrivateKey();
        if (privKey instanceof DERInteger)
        {
            DERInteger derD = DERInteger.getInstance(privKey);

            this.d = derD.getValue();
        }
        else
        {
            org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.sec.ECPrivateKey ec = org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privKey);

            this.d = ec.getKey();
            this.publicKey = ec.getPublicKey();
        }
    }

    public String getAlgorithm()
    {
        return algorithm;
    }

    /**
     * return the encoding format we produce in getEncoded().
     *
     * @return the string "PKCS#8"
     */
    public String getFormat()
    {
        return "PKCS#8";
    }

    /**
     * Return a PKCS8 representation of the key. The sequence returned
     * represents a full PrivateKeyInfo object.
     *
     * @return a PKCS8 representation of the key.
     */
    public byte[] getEncoded()
    {
        X962Parameters params;

        if (ecSpec instanceof ECNamedCurveSpec)
        {
            DERObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)ecSpec).getName());
            if (curveOid == null)  // guess it's the OID
            {
                curveOid = new DERObjectIdentifier(((ECNamedCurveSpec)ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
        }
        else if (ecSpec == null)
        {
            params = new X962Parameters(DERNull.INSTANCE);
        }
        else
        {
            ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());

            X9ECParameters ecP = new X9ECParameters(
                curve,
                EC5Util.convertPoint(curve, ecSpec.getGenerator(), withCompression),
                ecSpec.getOrder(),
                BigInteger.valueOf(ecSpec.getCofactor()),
                ecSpec.getCurve().getSeed());

            params = new X962Parameters(ecP);
        }

        PrivateKeyInfo info;
        org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.sec.ECPrivateKey keyStructure;

        if (publicKey != null)
        {
            keyStructure = new org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.sec.ECPrivateKey(this.getS(), publicKey, params);
        }
        else
        {
            keyStructure = new org.jenkinsci.plugins.iosbuilder.bouncycastle.asn1.sec.ECPrivateKey(this.getS(), params);
        }

        try
        {
            if (algorithm.equals("DSTU4145"))
            {
                info = new PrivateKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, params.toASN1Primitive()), keyStructure.toASN1Primitive());
            }
            else
            {

                info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params.toASN1Primitive()), keyStructure.toASN1Primitive());
            }

            return info.getEncoded(ASN1Encoding.DER);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public ECParameterSpec getParams()
    {
        return ecSpec;
    }

    public org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.spec.ECParameterSpec getParameters()
    {
        if (ecSpec == null)
        {
            return null;
        }

        return EC5Util.convertSpec(ecSpec, withCompression);
    }

    org.jenkinsci.plugins.iosbuilder.bouncycastle.jce.spec.ECParameterSpec engineGetSpec()
    {
        if (ecSpec != null)
        {
            return EC5Util.convertSpec(ecSpec, withCompression);
        }

        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public BigInteger getS()
    {
        return d;
    }

    public BigInteger getD()
    {
        return d;
    }

    public void setBagAttribute(
        ASN1ObjectIdentifier oid,
        ASN1Encodable attribute)
    {
        attrCarrier.setBagAttribute(oid, attribute);
    }

    public ASN1Encodable getBagAttribute(
        ASN1ObjectIdentifier oid)
    {
        return attrCarrier.getBagAttribute(oid);
    }

    public Enumeration getBagAttributeKeys()
    {
        return attrCarrier.getBagAttributeKeys();
    }

    public void setPointFormat(String style)
    {
        withCompression = !("UNCOMPRESSED".equalsIgnoreCase(style));
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof BCDSTU4145PrivateKey))
        {
            return false;
        }

        BCDSTU4145PrivateKey other = (BCDSTU4145PrivateKey)o;

        return getD().equals(other.getD()) && (engineGetSpec().equals(other.engineGetSpec()));
    }

    public int hashCode()
    {
        return getD().hashCode() ^ engineGetSpec().hashCode();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        String nl = System.getProperty("line.separator");

        buf.append("EC Private Key").append(nl);
        buf.append("             S: ").append(this.d.toString(16)).append(nl);

        return buf.toString();

    }

    private DERBitString getPublicKeyDetails(BCDSTU4145PublicKey pub)
    {
        try
        {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(pub.getEncoded()));

            return info.getPublicKeyData();
        }
        catch (IOException e)
        {   // should never happen
            return null;
        }
    }

    private void readObject(
        ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();

        byte[] enc = (byte[])in.readObject();

        populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));

        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(
        ObjectOutputStream out)
        throws IOException
    {
        out.defaultWriteObject();

        out.writeObject(this.getEncoded());
    }
}
