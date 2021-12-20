import gurobipy as gp
from gurobipy import GRB



class Caminho:
    def __init__(self, cenario, num_nos, num_cores, lista_nos):
        self.cenario = cenario;
        self.num_nos = num_nos;
        self.num_cores = num_cores;
        self.lista_nos = self.cria_lista_nos(lista_nos);
        self.recolorir;

    def cria_lista_nos(self, lista):
        aux  = []
        for n in lista:
            aux.append(self.vetoriza_numero(n))
        return aux
    
    def vetoriza_numero(self, num):
        aux = []
        for i in range(0, self.num_cores, 1):
            if i == num:
                aux.append(1)
            else:
                aux.append(0)
        return aux

def extrai_cenario(path):
    aux = path.split("/")
    return aux[len(aux)-1].split(".")[0]

def carrega_dados(path):
    with open(path) as file:
        lines = file.readlines()
        lines = [line.rstrip() for line in lines]
    
    cenario = extrai_cenario(path)
    num_nos = int(lines[0].split(" ")[0])
    num_cores = int(lines[0].split(" ")[1])
    del lines[0]
    lista_nos = [int(i) for i in lines]

    return Caminho(cenario, num_nos, num_cores, lista_nos)

def funcao_objetivo(caminho):
    soma = 0;
    for i in range(0, caminho.num_nos, 1):
        for j in range(0, caminho.num_cores, 1):
            soma =+ caminho.lista_nos[i][j]*caminho.recolorir[i][j]
    return soma

def otimiza_modelo(caminho):
    m = gp.Model("recolorir_caminho")

    #Criando variaveis binarias
    caminho.recolorir = []
    for i in range(0, caminho.num_nos, 1):
        aux = []
        for j in range(0, caminho.num_cores, 1):
            aux.append(m.addVar(vtype=gp.GRB.BINARY))
        caminho.recolorir.append(aux)

    #Motando função objetivo
    m.setObjective(funcao_objetivo(caminho), GRB.MAXIMIZE)

    

    



#importando dados
dados = carrega_dados("C:/Users/acere/Desktop/Projetos/Faculdade/LOC/EP2-LOC/entradas/entrada_01_25_5.txt")

otimiza_modelo(dados)

