package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.DetalleCompraVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleCompraDAO extends AbstractJdbcDao {

    private static final RowMapper<DetalleCompraVO> ROW_MAPPER = (rs, rowNum) -> new DetalleCompraVO(
            rs.getInt("id_detalle_compra"),
            rs.getInt("id_compra"),
            rs.getInt("id_producto"),
            rs.getInt("cantidad"),
            rs.getBigDecimal("costo_unitario"),
            rs.getBigDecimal("subtotal"));

    public DetalleCompraDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<DetalleCompraVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM detalle_compras ORDER BY id_detalle_compra", ROW_MAPPER);
    }

    public Optional<DetalleCompraVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM detalle_compras WHERE id_detalle_compra = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public DetalleCompraVO insert(DetalleCompraVO detalle) {
        int id = insertReturningId("""
                INSERT INTO detalle_compras (id_compra, id_producto, cantidad, costo_unitario)
                VALUES (?, ?, ?, ?)
                """,
                detalle.idCompra(), detalle.idProducto(), detalle.cantidad(), detalle.costoUnitario());
        return findById(id).orElseThrow(
                () -> new DataRetrievalFailureException("Inserted purchase detail was not found"));
    }

}
