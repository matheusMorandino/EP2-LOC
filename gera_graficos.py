import os
from os.path import isfile, join
from mpl_toolkits import mplot3d
import numpy as np
import matplotlib.pyplot as plt
import json


def carrega_json(path):
    f = open(path)
    data = json.load(f)
    f.close()
    return data

def formata_cien_para_decimal(num):
    return float("{:.7f}".format(float(num)))

def retorna_tamanho_cenario(str):
    return int(str.split("_")[2])

def retorna_cores_cenario(str):
    return int(str.split("_")[3].split(".")[0])

def extrai_nome(str):
    return str.split("\\")[len(str.split("\\"))-1]


MY_PATH = r"C:\Users\acere\Desktop\Projetos\Faculdade\LOC\EP2-LOC\resultadosM2_ajustados"

arquivos = []
for file in os.listdir(MY_PATH):
    if file.endswith(".json"):
        arquivos.append(extrai_nome(os.path.join(MY_PATH, file)))

#Carregando cenários em lista
cenarios = []
for f in arquivos:
    cenarios.append(carrega_json(MY_PATH + "\\" + f))

#Criando lista com tempos de execução
tempo = []
for cen in cenarios:
    tempo.append(formata_cien_para_decimal(cen['SolutionInfo']['Runtime']))

#Criando lista com o tamanho dos cenários
tamanho = []
for cen in arquivos:
    tamanho.append(retorna_tamanho_cenario(cen))

#Criando lista com o tamanho dos cenários
cores = []
for cen in arquivos:
    cores.append(retorna_cores_cenario(cen))


#Desenhando gráfico
fig = plt.figure()
ax = plt.axes(projection='3d')

ax.plot_trisurf(cores, tamanho, tempo,alpha=1,
                                      linewidth=0,
                                      antialiased=True)

ax.scatter3D(cores, tamanho, tempo, c='Orange')
ax.set_xlabel('cores')
ax.set_ylabel('tamanho')
ax.set_zlabel('tempo')
plt.show()
