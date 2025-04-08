import logging
from collections import defaultdict

# Configuração de logging
logging.basicConfig(
    filename='torneio.log',
    filemode='w',
    format='%(asctime)s - %(levelname)s - %(message)s',
    level=logging.ERROR
)

class Partida:
    def __init__(self, time1, time2, gols1, gols2):
        self.time1 = time1
        self.time2 = time2
        self.gols1 = gols1
        self.gols2 = gols2

    def vencedor(self):
        if self.gols1 > self.gols2:
            return self.time1
        elif self.gols2 > self.gols1:
            return self.time2
        else:
            return None  # empate

    def __str__(self):
        return f"{self.time1} {self.gols1} x {self.gols2} {self.time2}"

class Torneio:
    def __init__(self):
        self.times = set()
        self.partidas = []

    def adicionarTime(self, nome):
        try:
            if not nome or not nome.strip():
                raise ValueError("Nome inválido")
            self.times.add(nome)
            print(f"✅ Time \"{nome}\" adicionado com sucesso!")
        except Exception as e:
            logging.error(f"Erro ao adicionar time: {e}")
            print("❌ Erro:", e)

    def criarPartida(self, time1, time2, gols1, gols2):
        try:
            if time1 not in self.times or time2 not in self.times:
                raise ValueError("Time não existe")
            if gols1 < 0 or gols2 < 0:
                raise ValueError("Número inválido de gols")
            partida = Partida(time1, time2, gols1, gols2)
            self.partidas.append(partida)
            print(f"✅ Partida entre \"{time1}\" e \"{time2}\" criada com sucesso!")
        except Exception as e:
            logging.error(f"Erro ao criar partida: {e}")
            print("❌ Erro:", e)

    def jogar(self):
        pontos = defaultdict(int)
        for partida in self.partidas:
            vencedor = partida.vencedor()
            if vencedor:
                pontos[vencedor] += 3
            else:
                pontos[partida.time1] += 1
                pontos[partida.time2] += 1

        return ResultadoTorneio(pontos, self.partidas)

class ResultadoTorneio:
    def __init__(self, classificacao, partidas):
        self.classificacao = classificacao
        self.partidas = partidas

    def imprimirClassificacao(self):
        print("\nClassificação Final:")
        ordenado = sorted(self.classificacao.items(), key=lambda x: -x[1])
        for i, (time, pontos) in enumerate(ordenado, 1):
            print(f"{i}. {time} ({pontos} pontos)")

    def imprimirResultados(self):
        print("\nResultados:")
        for partida in self.partidas:
            print(partida)

class Main:
    @staticmethod
    def executar():
        torneio = Torneio()

        # Adicionando times
        torneio.adicionarTime("Brasil")
        torneio.adicionarTime("")  # ❌ Erro: Nome inválido
        torneio.adicionarTime("Canadá")
        torneio.adicionarTime("Argentina")
        torneio.adicionarTime("Angola")

        # Criando partidas
        torneio.criarPartida("Brasil", "Canadá", 1, 0)
        torneio.criarPartida("Argentina", "Angola", 0, 1)
        torneio.criarPartida("Brasil", "Argentina", -10, -2)  # ❌ Erro: Número inválido de gols
        torneio.criarPartida("Brasil", "Argentina", 0, 2)
        torneio.criarPartida("Angola", "Canadá", 1, 1)
        torneio.criarPartida("Brasil", "Angola", 3, 2)
        torneio.criarPartida("Argentina", "Nigéria", 3, 3)  # ❌ Erro: Time não existe
        torneio.criarPartida("Argentina", "Canadá", 2, 4)

        # Exibe resultados
        resultados = torneio.jogar()
        resultados.imprimirClassificacao()
        resultados.imprimirResultados()

if __name__ == "__main__":
    Main.executar()
