## Building Key Pair

1. **Creamos la clave Pública y Privada usando cryptography**:
```java
    public static KeyPair createWallet() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }
```
2. **Accedemos a la Info**:
```java
public static void main(String[] args) {
   Security.addProvider(new BouncyCastleProvider());
   try {
      LinkedList linkedList = new LinkedList();
      KeyPair keyPair = Context.createWallet();
      PrivateKey privateKey = keyPair.getPrivate();
      PublicKey publicKey = keyPair.getPublic();
      byte[] privateKeyBytes = privateKey.getEncoded();
      byte[] publicKeyBytes = publicKey.getEncoded();
      String privateKeyHex = bytesToHex(privateKeyBytes);
      String publicKeyHex = bytesToHex(publicKeyBytes);
      System.out.println("clave publica: " + publicKeyHex);
      System.out.println("clave privada: " + privateKeyHex);
   } catch (Exception e) {
      e.printStackTrace();
   }
}

private static String bytesToHex(byte[] bytes) {
   StringBuilder hexString = new StringBuilder(2 * bytes.length);
   for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
         hexString.append('0');
      }
      hexString.append(hex);
   }
   return hexString.toString();
}
```

## Proceso de verificación

1. **Firma digital**:
    - El usuario firma un mensaje o datos utilizando su clave privada
    - Esto genera una firma única que solo podría crearse con esa clave privada

2. **Verificación de la firma**:
    - Cualquiera que tenga la clave pública puede verificar que la firma fue creada con la clave privada correspondiente
    - Si la verificación es exitosa, confirma que el firmante posee la clave privada

En código, esto se implementa así:

```java
// El usuario firma un mensaje con su clave privada
public static byte[] firmarMensaje(PrivateKey clavePrivada, String mensaje) throws Exception {
    Signature firma = Signature.getInstance("SHA256withECDSA");
    firma.initSign(clavePrivada);
    firma.update(mensaje.getBytes());
    return firma.sign();
}

// Cualquiera puede verificar la firma con la clave pública
public static boolean verificarFirma(PublicKey clavePublica, String mensaje, byte[] firma) throws Exception {
    Signature verificador = Signature.getInstance("SHA256withECDSA");
    verificador.initVerify(clavePublica);
    verificador.update(mensaje.getBytes());
    return verificador.verify(firma);
}
```

## Ejemplo de uso en una blockchain:

1. **El usuario quiere enviar fondos**:
    - Crea una transacción (destino, cantidad, etc.)
    - Firma esta transacción con su clave privada

2. **El sistema verifica la propiedad**:
    - Toma la dirección del remitente (derivada de su clave pública)
    - Verifica que la firma de la transacción es válida usando la clave pública
    - Si la verificación es exitosa, confirma que el usuario es propietario de esa dirección

```java
// Ejemplo de verificación de transacción
public boolean verificarTransaccion(Transaccion tx) {
    // Obtener clave pública del remitente (desde la transacción o la blockchain)
    PublicKey clavePublica = tx.getClavePublicaRemitente();
    
    // Datos originales de la transacción (sin la firma)
    byte[] datosOriginales = tx.getDatosSinFirma();
    
    // Firma proporcionada
    byte[] firma = tx.getFirma();
    
    // Verificar que la firma corresponde a los datos y a la clave pública
    try {
        Signature verificador = Signature.getInstance("SHA256withECDSA");
        verificador.initVerify(clavePublica);
        verificador.update(datosOriginales);
        return verificador.verify(firma);
    } catch (Exception e) {
        return false;
    }
}
```

La belleza de este sistema es que permite verificar la propiedad sin que el usuario tenga que revelar su clave privada. Solo necesita demostrar que puede producir firmas válidas que se puedan verificar con su clave pública.

Esta es la base de la seguridad en sistemas blockchain: puedes probar que eres el propietario de una dirección sin compartir jamás tu clave privada.​​​​​​​​​​​​​​​​