package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.UsuarioDAO;
import com.uniremington.essenzagold.model.vo.UsuarioVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioRestController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public List<UsuarioResponse> findAll() {
        return usuarioDAO.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Integer id) {
        return usuarioDAO.findById(id)
                .map(UsuarioResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@RequestBody UsuarioVO usuario) {
        UsuarioVO created = usuarioDAO.insert(usuario);
        return ResponseEntity.created(URI.create("/api/usuarios/" + created.idUsuario()))
                .body(UsuarioResponse.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody UsuarioVO usuario) {
        if (!id.equals(usuario.idUsuario())) {
            return ResponseEntity.badRequest().build();
        }
        return usuarioDAO.update(usuario) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return usuarioDAO.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public record UsuarioResponse(
            Integer idUsuario,
            String nombre,
            String apellido,
            String documento,
            String correo,
            String telefono,
            String direccion,
            UsuarioVO.Rol rol,
            UsuarioVO.Estado estado,
            java.time.LocalDateTime fechaRegistro) {

        private static UsuarioResponse from(UsuarioVO usuario) {
            return new UsuarioResponse(
                    usuario.idUsuario(),
                    usuario.nombre(),
                    usuario.apellido(),
                    usuario.documento(),
                    usuario.correo(),
                    usuario.telefono(),
                    usuario.direccion(),
                    usuario.rol(),
                    usuario.estado(),
                    usuario.fechaRegistro());
        }
    }
}
