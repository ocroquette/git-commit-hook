import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificatesDownloader {

    static char[] passphrase = "changeit".toCharArray();
    private boolean verbose = false;

    CertificatesDownloader() {
    }

    CertificatesDownloader(boolean verbose) {
        this.verbose = verbose;
    }

    void log(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    public void downloadToTruststore(String hostname, int port, File keystoreFile) throws Exception {

        log(hostname);

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, passphrase);

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        log("Opening connection to " + hostname + ":" + port + "...");
        SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
        socket.setSoTimeout(10000);
        try {
            log("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            log("No errors, certificate is already trusted");
        } catch (SSLException e) {
            // Don't show the error in case of SSL issue, the certificate is probably not trusted
            // e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            throw new RuntimeException("Could not obtain server certificate chain");
        }

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        log("Server sent " + chain.length + " certificate(s):");

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];
            log
                    (" " + (i + 1) + " Subject " + cert.getSubjectDN());
            log("   Issuer  " + cert.getIssuerDN());
            sha1.update(cert.getEncoded());
            log("   sha1    " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            log("   md5     " + toHexString(md5.digest()));

            String alias = hostname + "-" + (i + 1);
            ks.setCertificateEntry(alias, cert);

            log(cert.toString());
            log("Added certificate to keystore using alias '" + alias + "'");
        }
        OutputStream out = new FileOutputStream(keystoreFile);
        ks.store(out, passphrase);
        out.close();
        log("Truststore saved to " + keystoreFile.getCanonicalPath());
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            /**
             * This change has been done due to the following resolution advised for Java 1.7+
             http://infposs.blogspot.kr/2013/06/installcert-and-java-7.html
             **/

            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
