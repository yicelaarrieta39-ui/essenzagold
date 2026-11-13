package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.CompraVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CompraDAO extends AbstractJdbcDao {

    private static final RowMapper<CompraVO> ROW_MAPPER = (rs, rowNum) -> new CompraVO(
            rs.getInt("id_compra"),
            rs.getInt("id_proveedor"),
            rs.getInt("id_usuario"),
            localDateTime(rs, "fecha_compra"),
            rs.getString("numero_factura"),
            rs.getBigDecimal("total"),
            enumValue(CompraVO.MetodoPago.class, rs.getString("metodo_pago")),
            enumValue(CompraVO.Estado.class, rs.getString("estado")),
            rs.getString("observaciones"));

    public CompraDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CompraVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM compras ORDER BY id_compra", ROW_MAPPER);
    }

    public Optional<CompraVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM compras WHERE id_compra = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public CompraVO insert(CompraVO compra) {
        int id = insertReturningId("""
                INSERT INTO compras
                    (id_proveedor, id_usuario, fecha_compra, numero_factura, total, metodo_pago, estado, observaciones)
                VALUES (?, ?, COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?, ?, ?)
                """,
                compra.idProveedor(), compra.idUsuario(), compra.fechaCompra(), compra.numeroFactura(), compra.total(),
                enumName(compra.metodoPago()), enumName(compra.estado()), compra.observaciones());
        return findById(id).orElseThrow(() -> new DataRetrievalFailureException("Inserted purchase was not found"));
    }

    public boolean update(CompraVO compra) {
        return jdbcTemplate.update("""
                UPDATE compras
                SET id_proveedor = ?, id_usuario = ?, numero_factura = ?, metodo_pago = ?, observaciones = ?
                WHERE id_compra = ?
                """,
                compra.idProveedor(), compra.idUsuario(), compra.numeroFactura(), enumName(compra.metodoPago()),
                compra.observaciones(), compra.idCompra()) > 0;
    }
}
