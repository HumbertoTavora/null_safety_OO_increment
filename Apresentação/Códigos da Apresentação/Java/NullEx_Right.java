public class NullEx_Right {
        public static void main(String[] args) {
        String nome = null; // a variável existe, mas não aponta para nenhum objeto
        
        if (nome != null) {
            System.out.println("Tamanho do nome: " + nome.length());
        } else {
            System.out.println("Nome ainda não definido.");
        }
    }
}
