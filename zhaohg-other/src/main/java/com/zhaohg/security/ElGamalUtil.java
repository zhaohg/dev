package com.zhaohg.security;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.spec.DHParameterSpec;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class ElGamalUtil {

    public static void main(String[] args) {
        try {
            //公钥加密，私钥解密
            Security.addProvider(new BouncyCastleProvider());

            //初始化秘钥
            AlgorithmParameterGenerator algorithmParameterGenerator = AlgorithmParameterGenerator.getInstance("ElGamal");
            AlgorithmParameters algorithmParameters = algorithmParameterGenerator.generateParameters();
            DHParameterSpec dhParameterSpec = (DHParameterSpec) algorithmParameters.getParameterSpec(DHParameterSpec.class);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ElGamal");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey elPublicKey = keyPair.getPublic();
            PrivateKey elPrivateKey = keyPair.getPrivate();
            System.out.println("Public Key : " + Base64.encodeBase64String(elPublicKey.getEncoded()));
            System.out.println("Private Key : " + Base64.encodeBase64String(elPrivateKey.getEncoded()));
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
