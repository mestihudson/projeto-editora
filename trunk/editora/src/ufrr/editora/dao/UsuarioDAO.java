package ufrr.editora.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ufrr.editora.entity.Usuario;

//@author Macksuel Lopes

public class UsuarioDAO {
	
	public Usuario existe(Usuario usuario) {
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		
		Query query = em.createQuery("from Usuario u where u.login = " + ":pLogin and u.senha = MD5(:pSenha)");
		query.setParameter("pLogin", usuario.getLogin());
		query.setParameter("pSenha", usuario.getSenha());
		
		Usuario login = new Usuario();
		if(!query.getResultList().isEmpty()){
			login = (Usuario) query.getSingleResult();
//			if(login.isStatus()){
//				System.out.println("Usuário Logado");
//			}

		} else {
			login = null;
		}
		
		em.getTransaction().commit();
		em.close();
		
		return login;
	}
	
	public Usuario trocaSenha(Usuario usuario) {
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		
		Query query = em.createQuery("from Usuario u where u.cpf = " + ":pCpf and u.nascimento = :pNascimento");
		query.setParameter("pCpf", usuario.getCpf());
		query.setParameter("pNascimento", usuario.getNascimento());
		
		Usuario login = new Usuario();
		if(!query.getResultList().isEmpty()){
			login = (Usuario) query.getSingleResult();
//			if(login.isStatus()){
//				System.out.println("Usuário Logado");
//			}

		} else {
			login = null;
		}
		
		em.getTransaction().commit();
		em.close();
		
		return login;
	}
	
}
