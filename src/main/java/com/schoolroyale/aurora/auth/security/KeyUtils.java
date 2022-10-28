package com.schoolroyale.aurora.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class KeyUtils {

    private final Environment environment;

    @Value("${aurora.jwt.access-token-private-path}")
    private String accessTokenPrivateKeyPath;

    @Value("${aurora.jwt.access-token-public-path}")
    private String accessTokenPublicKeyPath;

    @Value("${aurora.jwt.refresh-token-private-path}")
    private String refreshTokenPrivateKeyPath;

    @Value("${aurora.jwt.refresh-token-public-path}")
    private String refreshTokenPublicKeyPath;

    private KeyPair _accessTokenKeyPair;
    private KeyPair _refreshTokenKeyPair;

    public KeyUtils(Environment environment) {
        this.environment = environment;
    }

    private KeyPair getAccessTokenKeyPair() {
        if (Objects.isNull(_accessTokenKeyPair)) {
            _accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
        }
        return _accessTokenKeyPair;
    }

    private KeyPair getRefreshTokenKeyPair() {
        if (Objects.isNull(_refreshTokenKeyPair)) {
            _refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
        }
        return _refreshTokenKeyPair;
    }

    private KeyPair getKeyPair(String publicKeyPath, String privateKeyPath) {
        KeyPair keyPair;

        var publicKeyFile = new File(publicKeyPath);
        var privateKeyFile = new File(privateKeyPath);

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            log.info("loading keys from file: {}, {}", publicKeyPath, privateKeyPath);
            try {
                var keyFactory = KeyFactory.getInstance("RSA");
                var publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

                var publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                var publicKey = keyFactory.generatePublic(publicKeySpec);

                var privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
                var privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                var privateKey = keyFactory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                return keyPair;
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
                throw new RuntimeException("public and private keys don't exist");
            }
        }

        var directory = new File("access-refresh-token-keys");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            log.info("Generating new public and private keys: {}, {}", publicKeyPath, privateKeyPath);
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

            try (var fos = new FileOutputStream(publicKeyPath)) {
                var keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            try (var fos = new FileOutputStream(privateKeyPath)) {
                var keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }


    public RSAPublicKey getAccessTokenPublicKey() {
        return (RSAPublicKey) getAccessTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getAccessTokenPrivateKey() {
        return (RSAPrivateKey) getAccessTokenKeyPair().getPrivate();
    }

    public RSAPublicKey getRefreshTokenPublicKey() {
        return (RSAPublicKey) getRefreshTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        return (RSAPrivateKey) getRefreshTokenKeyPair().getPrivate();
    }
}
