import csv
import os


def fetchOutBookInfo(input_path, output_path):
    book_info = []

    with open(input_path, encoding="ISO-8859-1") as file:
        reader = csv.reader(file)
        for row in reader:
            title = row[3].lstrip("\'").lstrip("\"").rstrip("\'").rstrip("\"")
            book_info.append(title)
    print(len(book_info))
    with open(output_path, 'a+') as file:
        # writer = csv.writer(file)
        # for row in book_info:
        #     print(row)
        #     writer.write(row)
        for num, row in enumerate(book_info):
            # print(num)
            if 197000 < num <=207000:
            # if 206900 < num <=207000:
                file.write(row + '\n')
            # if num == len(book_info) - 1:
            #     file.write(row)
            # else:
            #     file.write(row + '\n')
            

         


if __name__ == "__main__":
    target_folder = "./original"
    output_path = "./generated/fpTestdata.csv"

    if os.path.exists(output_path):
        os.remove(output_path)

    for file in os.listdir(target_folder):
        if file.endswith("csv"):
            input_path = os.path.join(target_folder, file)
            fetchOutBookInfo(input_path, output_path)
