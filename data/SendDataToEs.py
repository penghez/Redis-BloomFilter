import os
import csv
from elasticsearch import Elasticsearch
from elasticsearch import helpers

es = Elasticsearch('http://34.74.169.35:9200/')


def insertToEs(file_path):
    es_formart_data = []
    with open(file_path) as file:
        reader = csv.reader(file)

        for num, row in enumerate(reader):
            if num % 10000 == 0:
                helpers.bulk(es, es_formart_data)
                es_formart_data = []
            else:
                es_formart_data.append({
                    "_index": "books",
                    "_type": "_doc",
                    "_source": {
                        "title": row
                    }
                })

        if len(es_formart_data) != 0:
            helpers.bulk(es, es_formart_data)


if __name__ == "__main__":
    file_path = "./generated/data.csv"
    insertToEs(file_path)
