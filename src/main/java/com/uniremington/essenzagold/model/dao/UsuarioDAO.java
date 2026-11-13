package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.UsuarioVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAO extends AbstractJdbcDao {

    private static final RowMapper<UsuarioVO> ROW_MAPPER = (rs, rowNum) -> new UsuarioVO(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("documento"),
            rs.getString("correo"),
            rs.getString("telefono"),
            rs.getString("direccion"),
            rs.getString("contrasena"),
            enumValue(UsuarioVO.Rol.class, rs.getString("rol")),
            enumValue(UsuarioVO.Estado.class, rs.getString("estado")),
            localDateTime(rs, "fecha_registro"));

    public UsuarioDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UsuarioVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM usuarios ORDER BY id_usuario", ROW_MAPPER);
    }

    public Optional<UsuarioVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM usuarios WHERE id_usuario = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public UsuarioVO insert(UsuarioVO usuario) {
        int id = insertReturningId("""
                INSERT INTO usuarios
                    (nombre, apellido, documento, correo, telefono, direccion, contrasena, rol, estado, fecha_registro)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))
                """,
                usuario.nombre(), usuario.apellido(), usuario.documento(), usuario.correo(), usuario.telefono(),
                usuario.direccion(), usuario.contrasena(), enumName(usuario.rol()), enumName(usuario.estado()),
                usuario.fechaRegistro());
        return findById(id).orElseThrow(() -> new DataRetrievalFailureException("Inserted user was not found"));
    }

    public boolean update(UsuarioVO usuario) {
        return jdbcTemplate.update("""
                UPDATE usuarios
                SET nombre = ?, apellido = ?, documento = ?, correo = ?, telefono = ?, direccion = ?,
                    contrasena = COALESCE(?, contrasena), rol = ?, estado = ?
                WHERE id_usuario = ?
                """,
                usuario.nombre(), usuario.apellido(), usuario.documento(), usuario.correo(), usuario.telefono(),
                usuario.direccion(), usuario.contrasena(), enumName(usuario.rol()), enumName(usuario.estado()),
                usuario.idUsuario()) > 0;
    }

    public boolean deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM usuarios WHERE id_usuario = ?", id) > 0;
    }
}
