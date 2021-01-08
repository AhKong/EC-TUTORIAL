package com.test.ompass.demo;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;



import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class EC {
    // 사용할 알고리즘 
    private final String ALGORITHM = "sect163k1";
   
    //키쌍 생성 메서드 
    public KeyPair generateKeyPair (){
       try {

           KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
           keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
           return keyGen.generateKeyPair();

       } catch (Exception  e){
           e.printStackTrace();
           return null;
       }
    }
   // 인증서 생성 메서드 
    public  X509Certificate generateCert(KeyPair pair, int days, String algorithm, String dn, String provider) throws Exception {
        // ex dn : C = "test" AU ="test" ... 
        // keytool을 통해 인증서를 생성해 봤다면 위의 설명이 뭔지 바로 이해가 갈듯요!! 
        X500Name issuerName = new X500Name(dn);
 
        BigInteger serial = BigInteger.valueOf(new SecureRandom().nextInt()).abs();
        Calendar calendar = Calendar.getInstance();
        Date startDate = new Date();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, days);

        Date endDate = calendar.getTime();
        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serial, startDate, endDate, issuerName, pair.getPublic());
        // 두번째 매개변수가 true 이면 신뢰 할 수 없는 인증서를 만드는 것임 ! 꼭 false 로 변경할 것 
        // 그 이유는 애초에 builder.addExtension 의 두번째 매개변수명이 isCritical 임 
        builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));

        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
        builder.addExtension(Extension.keyUsage, false, usage);

        ASN1EncodableVector purposes = new ASN1EncodableVector();
        purposes.add(KeyPurposeId.id_kp_serverAuth);
        purposes.add(KeyPurposeId.id_kp_clientAuth);
        purposes.add(KeyPurposeId.anyExtendedKeyUsage);
        builder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));
        ContentSigner contentSigner = new JcaContentSignerBuilder(algorithm).build(pair.getPrivate());

        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
        if (provider != null)
            converter.setProvider(provider);
        // 인증서를 생성하는 코드 !    
        X509Certificate cert = converter.getCertificate(builder.build(contentSigner));
        cert.checkValidity(new Date());
        cert.verify(pair.getPublic());

        return cert;
    }
}
