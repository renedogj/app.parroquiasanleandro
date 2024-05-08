from os.path import dirname, join
import base64
import json
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding

names_encripted_data = ("id", "nombre", "email", "fecha_nacimiento")

private_key = rsa.generate_private_key(
    public_exponent=65537,
    key_size=2048,
)

public_key = private_key.public_key()


def get_public_key():
    return public_key.public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo
    ).decode('utf-8').replace("\n", "")


def encrypt_payload(payload):
    payload = payload.encode('utf-8')
    ## Obtenemos la publicKey del servidor
    with open(join(dirname(__file__), "./keys/serverPublicKey.pem"), "rb") as key_file:
        serverPublicKey = serialization.load_pem_public_key(
            key_file.read()
        )

    ## Encriptamos el mensaje
    payload = serverPublicKey.encrypt(
        payload,
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )

    return base64.b64encode(payload).decode("utf-8")


def decrypt_result(result):
    result_list = json.loads(result)
    for x in names_encripted_data:
        if result_list["usuario"][x] != "":
            result_list["usuario"][x] = private_key.decrypt(
                base64.b64decode(result_list["usuario"][x]),
                padding.OAEP(
                    mgf=padding.MGF1(algorithm=hashes.SHA256()),
                    algorithm=hashes.SHA256(),
                    label=None
                )
            ).decode('utf-8')

    return result_list
