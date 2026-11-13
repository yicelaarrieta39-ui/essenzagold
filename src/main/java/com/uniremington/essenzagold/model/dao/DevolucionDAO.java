package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.DevolucionVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DevolucionDAO extends AbstractJdbcDao {

    private static final RowMapper<DevolucionVO> ROW_MAPPER = (rs, rowNum) -> new DevolucionVO(
            rs.getInt("id_devolucion"),
            rs.getInt("id_detalle_venta"),
            rs.getInt("id_usuario"),
            localDateTime(rs, "fecha_devolucion"),
            rs.getInt("cantidad"),
            rs.getString("motivo"),
            enumValue(DevolucionVO.Estado.class, rs.getString("estado")),
            enumValue(DevolucionVO.EstadoProducto.class, rs.getString("estado_producto")),
            rs.getString("observaciones"));

    public DevolucionDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<DevolucionVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM devoluciones ORDER BY id_devolucion", ROW_MAPPER);
    }

    public Optional<DevolucionVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM devoluciones WHERE id_devolucion = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public DevolucionVO insert(DevolucionVO devolucion) {
        int id = insertReturningId("""
                INSERT INTO devoluciones
                    (id_detalle_venta, id_usuario, fecha_devolucion, cantidad, motivo, estado,
                     estado_producto, observaciones)
                VALUES (?, ?, COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?, ?, ?)
                """,
                devolucion.idDetalleVenta(), devolucion.idUsuario(), devolucion.fechaDevolucion(),
                devolucion.cantidad(), devolucion.motivo(), enumName(devolucion.estado()),
                enumName(devolucion.estadoProducto()), devolucion.observaciones());
        return findById(id).orElseThrow(
                () -> new DataRetrievalFailureException("Inserted return was not found"));
    }

}
