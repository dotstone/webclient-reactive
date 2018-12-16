package net.devtoon.dos.webclient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(ids = {"net.devtoon.dos:product-api-annotation:+:stubs:8080"}, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class ContractTest {

    private final WebClientAPI api = new WebClientAPI();

    @Test
    void testGetAllProducts() {
        List<Product> products = api.getAllProducts().collectList().block();
        assertThat(products)
                .hasSize(2)
                .contains(new Product("default-product-id-1", "default-product-1", 5))
                .contains(new Product("default-product-id-2", "default-product-2", 3));
    }

    @Test
    void testGetProduct() {
        Product p = api.getProduct("default-product-id-1").block().getBody();
        assertThat(p).isNotNull();
        assertThat(p.getId()).isEqualTo("default-product-id-1");
        assertThat(p.getName()).isEqualTo("default-product-1");
        assertThat(p.getPrice()).isEqualTo(5);
    }

    @Test
    void testCreateProduct() {
        Product newProduct = api.postNewProduct("default-product-id-3", "updated-product-3", 17)
                .block().getBody();
        assertThat(newProduct).isNotNull();
        assertThat(newProduct.getId()).isNotNull();
        assertThat(newProduct.getName()).isEqualTo("updated-product-3");
        assertThat(newProduct.getPrice()).isEqualTo(17);
    }

    @Test
    void testDeleteProduct() {
        api.deleteProduct("default-product-id-1").block();
    }

    @Test
    void deleteAllProducts() {
        api.deleteAllProducts().block();
    }

    @Test
    void testUpdateProduct() {
        Product p = api.updateProduct("default-product-id-1", "updated-product-1", 1337)
                .block();
        assertThat(p).isNotNull();
        assertThat(p.getId()).isEqualTo("default-product-id-1");
        assertThat(p.getName()).isEqualTo("updated-product-1");
        assertThat(p.getPrice()).isEqualTo(1337);
    }
}
