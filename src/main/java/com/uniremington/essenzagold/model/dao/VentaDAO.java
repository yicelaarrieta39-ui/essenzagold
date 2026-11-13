package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.VentaVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VentaDAO extends AbstractJdbcDao {

    private static final RowMapper<VentaVO> ROW_MAPPER = (rs, rowNum) -> new VentaVO(
            rs.getInt("id_venta"),
            rs.getInt("id_usuario"),
            rs.getString("nombre_cliente"),
            rs.getString("documento_cliente"),
            rs.getString("telefono_cliente"),
            localDateTime(rs, "fecha_venta"),
            rs.getBigDecimal("total"),
            enumValue(VentaVO.MetodoPago.class, rs.getString("metodo_pago")),
            enumValue(VentaVO.Estado.class, rs.getString("estado")),
            rs.getString("observaciones"));

    public VentaDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<VentaVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM ventas ORDER BY id_venta", ROW_MAPPER);
    }

    public Optional<VentaVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM ventas WHERE id_venta = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public VentaVO insert(VentaVO venta) {
        int id = insertReturningId("""
                INSERT INTO ventas
                    (id_usuario, nombre_cliente, documento_cliente, telefono_cliente, fecha_venta,
                     total, metodo_pago, estado, observaciones)
                VALUES (?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?, ?)
                """,
                venta.idUsuario(), venta.nombreCliente(), venta.documentoCliente(), venta.telefonoCliente(),
                venta.fechaVenta(), venta.total(), enumName(venta.metodoPago()), enumName(venta.estado()),
                venta.observaciones());
        return findById(id).orElseThrow(() -> new DataRetrievalFailureException("Inserted sale was not found"));
    }

    public boolean update(VentaVO venta) {
        return jdbcTemplate.update("""
                UPDATE ventas
                SET id_usuario = ?, nombre_cliente = ?, documento_cliente = ?, telefono_cliente = ?,
                    metodo_pago = ?, observaciones = ?
                WHERE id_venta = ?
                """,
                venta.idUsuario(), venta.nombreCliente(), venta.documentoCliente(), venta.telefonoCliente(),
                enumName(venta.metodoPago()), venta.observaciones(), venta.idVenta()) > 0;
    }
}
