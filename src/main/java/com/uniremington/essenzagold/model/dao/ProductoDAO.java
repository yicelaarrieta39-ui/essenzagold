package com.uniremington.essenzagold.model.dao;

import java.util.List;
import java.util.Optional;

import com.uniremington.essenzagold.model.vo.ProductoVO;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDAO extends AbstractJdbcDao {

    private static final RowMapper<ProductoVO> ROW_MAPPER = (rs, rowNum) -> new ProductoVO(
            rs.getInt("id_producto"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            enumValue(ProductoVO.Categoria.class, rs.getString("categoria")),
            rs.getString("presentacion"),
            rs.getBigDecimal("capacidad_ml"),
            rs.getString("concentracion"),
            rs.getBigDecimal("precio_compra"),
            rs.getBigDecimal("precio_venta"),
            rs.getInt("stock"),
            rs.getInt("stock_minimo"),
            enumValue(ProductoVO.Estado.class, rs.getString("estado")),
            localDateTime(rs, "fecha_registro"));

    public ProductoDAO(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProductoVO> findAll() {
        return jdbcTemplate.query("SELECT * FROM productos ORDER BY id_producto", ROW_MAPPER);
    }

    public Optional<ProductoVO> findById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM productos WHERE id_producto = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    public ProductoVO insert(ProductoVO producto) {
        int id = insertReturningId("""
                INSERT INTO productos
                    (codigo, nombre, descripcion, categoria, presentacion, capacidad_ml, concentracion,
                     precio_compra, precio_venta, stock, stock_minimo, estado, fecha_registro)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))
                """,
                producto.codigo(), producto.nombre(), producto.descripcion(), enumName(producto.categoria()),
                producto.presentacion(), producto.capacidadMl(), producto.concentracion(), producto.precioCompra(),
                producto.precioVenta(), producto.stock(), producto.stockMinimo(), enumName(producto.estado()),
                producto.fechaRegistro());
        return findById(id).orElseThrow(() -> new DataRetrievalFailureException("Inserted product was not found"));
    }

    public boolean updateCatalog(ProductoVO producto) {
        return jdbcTemplate.update("""
                UPDATE productos
                SET codigo = ?, nombre = ?, descripcion = ?, categoria = ?, presentacion = ?, capacidad_ml = ?,
                    concentracion = ?, precio_compra = ?, precio_venta = ?, stock_minimo = ?, estado = ?
                WHERE id_producto = ?
                """,
                producto.codigo(), producto.nombre(), producto.descripcion(), enumName(producto.categoria()),
                producto.presentacion(), producto.capacidadMl(), producto.concentracion(), producto.precioCompra(),
                producto.precioVenta(), producto.stockMinimo(), enumName(producto.estado()), producto.idProducto()) > 0;
    }

    public boolean deleteById(Integer id) {
        return jdbcTemplate.update("DELETE FROM productos WHERE id_producto = ?", id) > 0;
    }

    public void synchronizeAvailabilityWithStock(Integer idProducto) {
        jdbcTemplate.update("""
                UPDATE productos
                SET estado = CASE
                    WHEN stock = 0 AND estado = 'Disponible' THEN 'Agotado'
                    WHEN stock > 0 AND estado = 'Agotado' THEN 'Disponible'
                    ELSE estado
                END
                WHERE id_producto = ?
                """, idProducto);
    }
}
