package Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;

@Getter
@Setter
@AllArgsConstructor
public class Usuario {

    private String nome;

    private StatusUsuario status;

    private InetAddress endereco;



    public enum StatusUsuario {
        DISPONIVEL, NAO_PERTURBE, VOLTO_LOGO
    }
}



