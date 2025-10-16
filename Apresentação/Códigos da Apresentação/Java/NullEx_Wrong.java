public class NullEx_Wrong {
        public static void main(String[] args) {
        String nome = null; // a variável existe, mas não aponta para nenhum objeto
        System.out.println("Tamanho do nome: " + nome.length());
    }
}
