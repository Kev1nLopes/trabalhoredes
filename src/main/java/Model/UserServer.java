package Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserServer {



    private String nome;

    private Usuario.StatusUsuario status;

    private InetAddress endereco;

    private Integer port;

    public enum StatusUsuario {
        DISPONIVEL, NAO_PERTURBE, VOLTO_LOGO
    }




}
