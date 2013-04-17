package ufrr.editora.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="tb_email")
public class EnviaEmail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="usuario_destino")
	private Usuario destino;
	
	private String titulo;
	
	private String mensagem;
	
	@ManyToOne
	@JoinColumn(name="usuario_autor_envio")
	private Usuario usuario;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private Collection<EnviaEmail> emails = new ArrayList<EnviaEmail>();
	
	@Temporal(TemporalType.DATE)
	private Calendar dataEnvio = Calendar.getInstance();

	/** Get and Set **/
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getDestino() {
		return destino;
	}

	public void setDestino(Usuario destino) {
		this.destino = destino;
	}

	public Calendar getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Calendar dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Collection<EnviaEmail> getEmails() {
		return emails;
	}

	public void setEmails(Collection<EnviaEmail> emails) {
		this.emails = emails;
	}
	
	

}
