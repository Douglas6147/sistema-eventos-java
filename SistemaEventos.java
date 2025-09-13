import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Usuario {
    private String nome;
    private String email;
    private String cidade;

    public Usuario(String nome, String email, String cidade) {
        this.nome = nome;
        this.email = email;
        this.cidade = cidade;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCidade() { return cidade; }

    @Override
    public String toString() {
        return nome + " (" + email + ") - Cidade: " + cidade;
    }
}

class Evento implements Serializable {
    private String nome;
    private String endereco;
    private String categoria;
    private LocalDateTime horario;
    private String descricao;
    private List<Usuario> participantes;

    public Evento(String nome, String endereco, String categoria, LocalDateTime horario, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
        this.participantes = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public LocalDateTime getHorario() { return horario; }
    public List<Usuario> getParticipantes() { return participantes; }

    public void adicionarParticipante(Usuario u) {
        participantes.add(u);
    }

    public void removerParticipante(Usuario u) {
        participantes.remove(u);
    }

    @Override
    public String toString() {
        return String.format("Evento: %s | Endereço: %s | Categoria: %s | Horário: %s | Descrição: %s",
                nome, endereco, categoria, horario.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), descricao);
    }
}

public class SistemaEventos {
    private static List<Evento> eventos = new ArrayList<>();
    private static Usuario usuarioLogado;
    private static final String ARQUIVO_EVENTOS = "events.data";

    public static void main(String[] args) {
        carregarEventos();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Sistema de Eventos ===");
        System.out.print("Informe seu nome: ");
        String nome = sc.nextLine();
        System.out.print("Informe seu email: ");
        String email = sc.nextLine();
        System.out.print("Informe sua cidade: ");
        String cidade = sc.nextLine();
        usuarioLogado = new Usuario(nome, email, cidade);

        int opcao;
        do {
            System.out.println("\n1 - Cadastrar evento");
            System.out.println("2 - Listar eventos");
            System.out.println("3 - Participar de evento");
            System.out.println("4 - Cancelar participação");
            System.out.println("5 - Mostrar eventos que já ocorreram");
            System.out.println("6 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> cadastrarEvento(sc);
                case 2 -> listarEventos();
                case 3 -> participarEvento(sc);
                case 4 -> cancelarParticipacao(sc);
                case 5 -> mostrarEventosPassados();
                case 6 -> salvarEventos();
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 6);

        System.out.println("Sistema encerrado.");
    }

    private static void cadastrarEvento(Scanner sc) {
        System.out.print("Nome do evento: ");
        String nome = sc.nextLine();
        System.out.print("Endereço: ");
        String endereco = sc.nextLine();
        System.out.print("Categoria: ");
        String categoria = sc.nextLine();
        System.out.print("Data e hora (dd/MM/yyyy HH:mm): ");
        String dataHora = sc.nextLine();
        LocalDateTime horario = LocalDateTime.parse(dataHora, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        System.out.print("Descrição: ");
        String descricao = sc.nextLine();

        eventos.add(new Evento(nome, endereco, categoria, horario, descricao));
        System.out.println("Evento cadastrado com sucesso!");
    }

    private static void listarEventos() {
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        eventos.sort(Comparator.comparing(Evento::getHorario));
        for (int i = 0; i < eventos.size(); i++) {
            System.out.println((i + 1) + ". " + eventos.get(i));
        }
    }

    private static void participarEvento(Scanner sc) {
        listarEventos();
        System.out.print("Digite o número do evento para participar: ");
        int escolha = sc.nextInt();
        sc.nextLine();
        if (escolha > 0 && escolha <= eventos.size()) {
            eventos.get(escolha - 1).adicionarParticipante(usuarioLogado);
            System.out.println("Você está participando deste evento!");
        } else {
            System.out.println("Evento inválido.");
        }
    }

    private static void cancelarParticipacao(Scanner sc) {
        listarEventos();
        System.out.print("Digite o número do evento para cancelar: ");
        int escolha = sc.nextInt();
        sc.nextLine();
        if (escolha > 0 && escolha <= eventos.size()) {
            eventos.get(escolha - 1).removerParticipante(usuarioLogado);
            System.out.println("Participação cancelada.");
        } else {
            System.out.println("Evento inválido.");
        }
    }

    private static void mostrarEventosPassados() {
        LocalDateTime agora = LocalDateTime.now();
        System.out.println("Eventos já ocorridos:");
        eventos.stream()
                .filter(e -> e.getHorario().isBefore(agora))
                .sorted(Comparator.comparing(Evento::getHorario))
                .forEach(System.out::println);
    }

    private static void salvarEventos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_EVENTOS))) {
            oos.writeObject(eventos);
            System.out.println("Eventos salvos em arquivo.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar eventos: " + e.getMessage());
        }
    }

    private static void carregarEventos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_EVENTOS))) {
            eventos = (List<Evento>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            eventos = new ArrayList<>();
        }
    }
}
