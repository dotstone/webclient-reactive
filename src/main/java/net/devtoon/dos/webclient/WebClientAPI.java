package net.devtoon.dos.webclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

public class WebClientAPI {

    private WebClient webClient;

    public static void main(String[] args) throws InterruptedException {
        WebClientAPI api = new WebClientAPI();
//        api.postNewProduct("Sunstwos", 123)
//                .log()
//                .thenMany(api.getAllProducts())
//                .take(1)
//                .flatMap(p -> api.updateProduct(p.getId(), "Wos ondas", 321))
////                .flatMap(p -> api.deleteProduct(p.getId()))
//                .thenMany(api.getAllProducts())
//                .thenMany(api.getAllEvents())
//                .subscribe(System.out::println);

        api.getAllEvents()
                .flatMap(event -> api.postNewProduct(event.getEventType(), new Random().nextInt()))
                .take(5)
                .thenMany(api.getAllProducts())
                .subscribe(product -> System.err.println("Added product: " + product.getName() + " (" + product.getPrice() + ")"));
    }

     WebClientAPI() {
         this.webClient = WebClient.builder()
                 .baseUrl("http://localhost:8080/")
                 .build();
     }

     private Mono<ResponseEntity<Product>> postNewProduct(String name, int price) {
         return webClient.post()
                 .body(Mono.just(new Product(null, name, price)), Product.class)
                 .exchange()
                 .flatMap(response -> response.toEntity(Product.class))
                 .doOnSuccess(o -> System.out.println("***** POST " + o));
     }

     private Flux<Product> getAllProducts() {
         return webClient.get()
                 .retrieve()
                 .bodyToFlux(Product.class)
                 .doOnNext(o -> System.out.println("***** GET " + o));
     }

     private Mono<Product> updateProduct(String id, String name, int price) {
         return webClient.put()
                 .uri("/{id}", id)
                 .body(Mono.just(new Product(null, name, price)), Product.class)
                 .retrieve()
                 .bodyToMono(Product.class)
                 .doOnSuccess(o -> System.out.println("***** UPDATE " + o));
     }

     private Mono<Void> deleteProduct(String id) {
         return webClient
                 .delete()
                 .uri("/{id}", id)
                 .retrieve()
                 .bodyToMono(Void.class)
                 .doOnSuccess(o -> System.out.println("***** DELETE " + o));
     }

     private Flux<ProductEvent> getAllEvents() {
         return webClient.get()
                 .uri("/events")
                 .retrieve()
                 .bodyToFlux(ProductEvent.class);
     }
}
