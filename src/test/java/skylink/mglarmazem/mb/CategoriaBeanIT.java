/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package skylink.mglarmazem.mb;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import skylink.armazem.modelo.CategoriaProduto;

/**
 *
 * @author TECNICO
 */
public class CategoriaBeanIT {
    
    public CategoriaBeanIT() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of init method, of class CategoriaBean.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        CategoriaBean instance = new CategoriaBean();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getListaCategorias method, of class CategoriaBean.
     */
    @Test
    public void testGetListaCategorias() {
        System.out.println("getListaCategorias");
        CategoriaBean instance = new CategoriaBean();
        List<CategoriaProduto> expResult = null;
        List<CategoriaProduto> result = instance.getListaCategorias();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
