import csv
import os


def fetchOutBookInfo(input_path, output_path):
    book_info = []

    with open(input_path, encoding="ISO-8859-1") as file:
        reader = csv.reader(file)
        for row in reader:
            title = row[3].lstrip("\'").lstrip("\"").rstrip("\'").rstrip("\"")
            print(title)
            book_info.append([title])

    with open(output_path, 'a+') as file:
        writer = csv.writer(file)
        writer.writerows(book_info)


if __name__ == "__main__":
    target_folder = "./original"
    output_path = "./generated/data.csv"

    if os.path.exists(output_path):
        os.remove(output_path)

    for file in os.listdir(target_folder):
        if file.endswith("csv"):
            input_path = os.path.join(target_folder, file)
            fetchOutBookInfo(input_path, output_path)
