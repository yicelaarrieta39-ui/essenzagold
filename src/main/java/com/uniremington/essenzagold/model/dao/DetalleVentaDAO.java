package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.DetalleVentaVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DetalleVentaDAO extends AbstractJdbcDao {

    private static final RowMapper<DetalleVentaVO> ROW_MAPPER = (rs, rowNum) -> new DetalleVentaVO(
            rs.getInt("id_detalle_venta"),
            rs.getInt("id_venta"),
            rs.getInt("id_producto"),
            rs.getInt("cantidad"),
            rs.getBigDecimal("precio_unitario"),
            rs.getBigDecimal("subtotal"));

    public DetalleVentaDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<DetalleVentaVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM detalle_ventas ORDER BY id_detalle_venta", ROW_MAPPER);
    }

    public Optional<DetalleVentaVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM detalle_ventas WHERE id_detalle_venta = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public DetalleVentaVO insert(DetalleVentaVO detalle) {
        int id = insertReturningId("""
                INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario)
                VALUES (?, ?, ?, ?)
                """,
                detalle.idVenta(), detalle.idProducto(), detalle.cantidad(), detalle.precioUnitario());
        return findById(id).orElseThrow(
                () -> new DataRetrievalFailureException("Inserted sale detail was not found"));
    }

}
