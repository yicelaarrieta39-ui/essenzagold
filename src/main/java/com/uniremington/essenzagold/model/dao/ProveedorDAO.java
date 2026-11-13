package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.ProveedorVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProveedorDAO extends AbstractJdbcDao {

    private static final RowMapper<ProveedorVO> ROW_MAPPER = (rs, rowNum) -> new ProveedorVO(
            rs.getInt("id_proveedor"),
            rs.getString("nombre_empresa"),
            rs.getString("nit"),
            rs.getString("contacto"),
            rs.getString("telefono"),
            rs.getString("correo"),
            rs.getString("direccion"),
            rs.getString("ciudad"),
            enumValue(ProveedorVO.Estado.class, rs.getString("estado")),
            localDateTime(rs, "fecha_registro"));

    public ProveedorDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProveedorVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM proveedores ORDER BY id_proveedor", ROW_MAPPER);
    }

    public Optional<ProveedorVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM proveedores WHERE id_proveedor = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public ProveedorVO insert(ProveedorVO proveedor) {
        int id = insertReturningId("""
                INSERT INTO proveedores
                    (nombre_empresa, nit, contacto, telefono, correo, direccion, ciudad, estado, fecha_registro)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))
                """,
                proveedor.nombreEmpresa(), proveedor.nit(), proveedor.contacto(), proveedor.telefono(),
                proveedor.correo(), proveedor.direccion(), proveedor.ciudad(), enumName(proveedor.estado()),
                proveedor.fechaRegistro());
        return findById(id).orElseThrow(() -> new DataRetrievalFailureException("Inserted supplier was not found"));
    }

    public boolean update(ProveedorVO proveedor) {
        return jdbcTemplate.update("""
                UPDATE proveedores
                SET nombre_empresa = ?, nit = ?, contacto = ?, telefono = ?, correo = ?, direccion = ?,
                    ciudad = ?, estado = ?
                WHERE id_proveedor = ?
                """,
                proveedor.nombreEmpresa(), proveedor.nit(), proveedor.contacto(), proveedor.telefono(),
                proveedor.correo(), proveedor.direccion(), proveedor.ciudad(), enumName(proveedor.estado()),
                proveedor.idProveedor()) > 0;
    }

    public boolean deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM proveedores WHERE id_proveedor = ?", id) > 0;
    }
}
