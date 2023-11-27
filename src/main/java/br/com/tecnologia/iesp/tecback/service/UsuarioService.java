package br.com.tecnologia.iesp.tecback.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.tecnologia.iesp.tecback.dto.CartaoDTO;
import br.com.tecnologia.iesp.tecback.dto.CartaoDTOView;
import br.com.tecnologia.iesp.tecback.dto.UsuarioDTO;
import br.com.tecnologia.iesp.tecback.dto.UsuarioDTOView;
import br.com.tecnologia.iesp.tecback.entities.Cartao;
import br.com.tecnologia.iesp.tecback.entities.Email;
import br.com.tecnologia.iesp.tecback.entities.Usuario;
import br.com.tecnologia.iesp.tecback.exception.ApplicationServiceException;
import br.com.tecnologia.iesp.tecback.repository.CartaoRepository;
import br.com.tecnologia.iesp.tecback.repository.UsuarioRepository;
import br.com.tecnologia.iesp.tecback.util.CpfRgUtil;
import br.com.tecnologia.iesp.tecback.util.UtilidadesDesenvolvimento;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {


	private UsuarioRepository usuarioRepository;

	@Autowired
	private CartaoRepository cartaoRepository;

	@Autowired
	EmailService emailService;
	
    @Autowired
    public UsuarioService(@Qualifier("usuarioRepository") UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Value("${spring.mail.username}")
    private String emailDefault;

	public UsuarioDTO salvar(UsuarioDTO user) throws Exception {

		Usuario usuario = new Usuario();
		String senha = criptografarSenha(user.getSenha());

		if (StringUtils.isNotBlank(senha)) {
			usuario.setSenha(senha);
		}
		usuario.setNomeCompleto(user.getNomeCompleto());
		usuario.setEmail(user.getEmail());
		usuario.setDataNasc(user.getDataNasc());
		usuario.setLogin(user.getLogin());

		CartaoDTO cartaoDTO = user.getCartao();

		Cartao cartao = new Cartao();
		cartao.setCodSeguranca(cartaoDTO.getCodSeguranca());

		String cpfSemMascara = UtilidadesDesenvolvimento.retiraCpf(cartaoDTO.getCpf());

		if (CpfRgUtil.validaCPF(cpfSemMascara)) {
			cartao.setCpf(cpfSemMascara);
		} else {
			throw new ApplicationServiceException("message.erro.cpf.invalido");
		}

		verificarCPF(cpfSemMascara);

		cartao.setNumCartao(cartaoDTO.getNumCartao());
		cartao.setTitularNome(cartaoDTO.getTitularNome());
		cartao.setValidadeCartao(cartaoDTO.getValidadeCartao());
		
		usuario.setDadosCartao(cartao);
		
		cartaoRepository.save(cartao);
		usuarioRepository.save(usuario);

		//enviarEmail(usuario);
		return user;

	}
    public void verificarCPF(String cpf) throws ApplicationServiceException {
		Cartao cartao = cartaoRepository.verificarCpf(cpf);
		if (cartao != null) {
			throw new ApplicationServiceException("message.erro.cpf.existente");
		}
	}
    public void alterar(UsuarioDTO user, Long id) throws ApplicationServiceException {

        Optional<Usuario> op = usuarioRepository.findById(id);
        String senha = criptografarSenha(user.getSenha());

        if(!op.isEmpty()){

            Usuario usuario = op.get();
            usuario.setNomeCompleto(user.getNomeCompleto());
            usuario.setEmail(user.getEmail());
            usuario.setSenha(senha);
            usuarioRepository.save(usuario);

        }else{
            throw new ApplicationServiceException("message.erro.user.not.found");
        }

    }
    
    public List<UsuarioDTOView> listar() {
    	
       List<Usuario> user = usuarioRepository.findAll();
       
       List<UsuarioDTOView> retorno = new ArrayList<>();
       
		for (Usuario usuario : user) {

			UsuarioDTOView userDTO = new UsuarioDTOView();
			CartaoDTOView cartaoDTO = new CartaoDTOView();

			userDTO.setNomeCompleto(usuario.getNomeCompleto());
			userDTO.setDataNasc(usuario.getDataNasc());
			userDTO.setLogin(usuario.getLogin());
			userDTO.setSenha(usuario.getPassword());
			userDTO.setEmail(usuario.getEmail());

			cartaoDTO.setCodSeguranca(usuario.getDadosCartao().getCodSeguranca());
			cartaoDTO.setCpf(usuario.getDadosCartao().getCpf());
			cartaoDTO.setNumCartao(usuario.getDadosCartao().getNumCartao());
			cartaoDTO.setTitularNome(usuario.getDadosCartao().getTitularNome());
			cartaoDTO.setValidadeCartao(usuario.getDadosCartao().getValidadeCartao());

			userDTO.setCartao(cartaoDTO);

			retorno.add(userDTO);
		}
		
		return retorno;
    }
    
    public void excluir(Long id) throws ApplicationServiceException{

    	Optional<Usuario> usuario = usuarioRepository.findById(id);
    	
    	if(!usuario.isEmpty()) {
    		usuarioRepository.deleteById(usuario.get().getId());
    	}else {
    		throw new ApplicationServiceException("message.erro.user.not.found");
    	}
    }
    
    public Usuario consultarPorId(Long id) throws ApplicationServiceException  {
        
    	Usuario user = usuarioRepository.findById(id).get();
    	
    	if(user != null) {
    		return user;
    		
    	}else {
    		throw new ApplicationServiceException("message.erro.user.not.found");
    	}
    }

    public String criptografarSenha(String senha){

        return BCrypt.hashpw(senha, BCrypt.gensalt());

    }
    
	public void enviarEmail(Usuario user) throws ApplicationServiceException {

		try {

			Email emailModel = new Email();
			
			StringBuilder corpoEmail = new StringBuilder();

			corpoEmail.append("Prezado(a) ").append(user.getNomeCompleto()).append(",\n\n");
			corpoEmail.append("Gostaríamos de informar que seu cadastro foi"
					+ " realizado com sucesso. É um prazer tê-lo(a) como parte de nossa comunidade.\n\n");
			corpoEmail.append("Em caso de dúvidas ou necessidade de suporte,"
					+ " não hesite em entrar em contato conosco. Estamos sempre prontos para ajudar.\n\n");
			corpoEmail.append("Agradecemos por escolher nossos serviços e"
					+ " esperamos proporcionar uma experiência satisfatória" + " em todas as interações.\n\n");
			corpoEmail.append("Atenciosamente,\n");
			corpoEmail.append("Equipe TeckBack.");

			emailModel.setSubject("Cadastro TeckBack - UNIESP");
			emailModel.setEmailTo(user.getEmail());
			emailModel.setEmailFrom(emailDefault);
			emailModel.setOwnerRef(user.getId().toString() + " - " + user.getNomeCompleto());
			emailModel.setText(corpoEmail.toString());

			emailService.sendEmail(emailModel);

		} catch (Exception e) {
			throw new ApplicationServiceException("message.erro.envio.email");
		}
	}
}
