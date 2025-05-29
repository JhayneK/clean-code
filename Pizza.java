import java.util.*;

// Enums para tipagem segura
enum Tamanho {
    PEQUENA, MEDIA, GRANDE
}

enum TipoBorda {
    TRADICIONAL, RECHEADA
}

// Classe Pizza imutável
public class Pizza {
    private final Tamanho tamanho;
    private final TipoBorda borda;
    private final List<String> sabores;
    private final List<String> extras;
    private final boolean paraViagem;
    
    private Pizza(Builder builder) {
        this.tamanho = builder.tamanho;
        this.borda = builder.borda;
        this.sabores = Collections.unmodifiableList(new ArrayList<>(builder.sabores));
        this.extras = Collections.unmodifiableList(new ArrayList<>(builder.extras));
        this.paraViagem = builder.paraViagem;
    }
    
    // Getters
    public Tamanho getTamanho() { return tamanho; }
    public TipoBorda getBorda() { return borda; }
    public List<String> getSabores() { return sabores; }
    public List<String> getExtras() { return extras; }
    public boolean isParaViagem() { return paraViagem; }
    
    // Método estático para iniciar a construção
    public static TamanhoStep nova() {
        return new Builder();
    }
    
    // Interfaces para guiar a construção step-by-step
    public interface TamanhoStep {
        SaborStep tamanho(Tamanho tamanho);
        SaborStep pequena();
        SaborStep media();
        SaborStep grande();
    }
    
    public interface SaborStep {
        SaborStep adicionarSabor(String sabor);
        SaborStep comSabores(String... sabores);
        BorderStep sabores(List<String> sabores);
    }
    
    public interface BorderStep {
        ExtrasStep comBorda(TipoBorda borda);
        ExtrasStep bordaTradicional();
        ExtrasStep bordaRecheada();
    }
    
    public interface ExtrasStep {
        ExtrasStep adicionarExtra(String extra);
        ExtrasStep comExtras(String... extras);
        EntregaStep extras(List<String> extras);
        EntregaStep semExtras();
    }
    
    public interface EntregaStep {
        BuildStep paraViagem();
        BuildStep paraConsumoLocal();
    }
    
    public interface BuildStep {
        Pizza construir();
    }
    
    // Builder que implementa todas as interfaces
    public static class Builder implements TamanhoStep, SaborStep, BorderStep, 
                                          ExtrasStep, EntregaStep, BuildStep {
        private Tamanho tamanho;
        private TipoBorda borda = TipoBorda.TRADICIONAL;
        private List<String> sabores = new ArrayList<>();
        private List<String> extras = new ArrayList<>();
        private boolean paraViagem = false;
        
        @Override
        public SaborStep tamanho(Tamanho tamanho) {
            this.tamanho = tamanho;
            return this;
        }
        
        @Override
        public SaborStep pequena() {
            return tamanho(Tamanho.PEQUENA);
        }
        
        @Override
        public SaborStep media() {
            return tamanho(Tamanho.MEDIA);
        }
        
        @Override
        public SaborStep grande() {
            return tamanho(Tamanho.GRANDE);
        }
        
        @Override
        public SaborStep adicionarSabor(String sabor) {
            if (sabor != null && !sabor.trim().isEmpty()) {
                this.sabores.add(sabor.trim());
            }
            return this;
        }
        
        @Override
        public SaborStep comSabores(String... sabores) {
            for (String sabor : sabores) {
                adicionarSabor(sabor);
            }
            return this;
        }
        
        @Override
        public BorderStep sabores(List<String> sabores) {
            if (sabores != null) {
                for (String sabor : sabores) {
                    adicionarSabor(sabor);
                }
            }
            return this;
        }
        
        @Override
        public ExtrasStep comBorda(TipoBorda borda) {
            this.borda = borda;
            return this;
        }
        
        @Override
        public ExtrasStep bordaTradicional() {
            return comBorda(TipoBorda.TRADICIONAL);
        }
        
        @Override
        public ExtrasStep bordaRecheada() {
            return comBorda(TipoBorda.RECHEADA);
        }
        
        @Override
        public ExtrasStep adicionarExtra(String extra) {
            if (extra != null && !extra.trim().isEmpty()) {
                this.extras.add(extra.trim());
            }
            return this;
        }
        
        @Override
        public ExtrasStep comExtras(String... extras) {
            for (String extra : extras) {
                adicionarExtra(extra);
            }
            return this;
        }
        
        @Override
        public EntregaStep extras(List<String> extras) {
            if (extras != null) {
                for (String extra : extras) {
                    adicionarExtra(extra);
                }
            }
            return this;
        }
        
        @Override
        public EntregaStep semExtras() {
            this.extras.clear();
            return this;
        }
        
        @Override
        public BuildStep paraViagem() {
            this.paraViagem = true;
            return this;
        }
        
        @Override
        public BuildStep paraConsumoLocal() {
            this.paraViagem = false;
            return this;
        }
        
        @Override
        public Pizza construir() {
            validar();
            return new Pizza(this);
        }
        
        private void validar() {
            // Não permitir pizza sem tamanho
            if (tamanho == null) {
                throw new IllegalStateException("Pizza deve ter um tamanho definido");
            }
            
            // Toda pizza deve conter pelo menos um sabor
            if (sabores.isEmpty()) {
                throw new IllegalStateException("Pizza deve conter pelo menos um sabor");
            }
            
            // Pizzas pequenas não podem conter borda recheada
            if (tamanho == Tamanho.PEQUENA && borda == TipoBorda.RECHEADA) {
                throw new IllegalStateException("Pizzas pequenas não podem ter borda recheada");
            }
            
            // Validar quantidade de sabores por tamanho
            int maxSabores = getMaxSaboresPorTamanho(tamanho);
            if (sabores.size() > maxSabores) {
                throw new IllegalStateException(
                    String.format("Pizzas %s podem conter no máximo %d sabores", 
                                tamanho.name().toLowerCase(), maxSabores)
                );
            }
        }
        
        private int getMaxSaboresPorTamanho(Tamanho tamanho) {
            switch (tamanho) {
                case PEQUENA: return 2;
                case MEDIA: return 3;
                case GRANDE: return 4;
                default: return 1;
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format(
            "Pizza{tamanho=%s, borda=%s, sabores=%s, extras=%s, paraViagem=%s}",
            tamanho, borda, sabores, extras, paraViagem
        );
    }
}

// Classe de exemplo para demonstrar o uso
class ExemploPizza {
    public static void main(String[] args) {
        try {
            // Exemplo 1: Pizza básica
            Pizza pizza1 = Pizza.nova()
                .pequena()
                .adicionarSabor("Mussarela")
                .adicionarSabor("Calabresa")
                .bordaTradicional()
                .semExtras()
                .paraViagem()
                .construir();
            
            System.out.println("Pizza 1: " + pizza1);
            
            // Exemplo 2: Pizza elaborada
            Pizza pizza2 = Pizza.nova()
                .grande()
                .comSabores("Mussarela", "Calabresa", "Portuguesa", "Frango")
                .bordaRecheada()
                .comExtras("Queijo Extra", "Molho Extra")
                .paraConsumoLocal()
                .construir();
            
            System.out.println("Pizza 2: " + pizza2);
            
            // Exemplo 3: Tentativa de criar pizza inválida (vai gerar exceção)
            try {
                Pizza pizzaInvalida = Pizza.nova()
                    .pequena()
                    .adicionarSabor("Mussarela")
                    .bordaRecheada() // Erro: pizza pequena com borda recheada
                    .paraConsumoLocal()
                    .construir();
            } catch (IllegalStateException e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}