SRC_DIR = src
BIN_DIR = bin

MAIN_CLASS = app.Main

all: compile run

compile:
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/app/Main.java

run:
	java -cp $(BIN_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)/*

.SILENT: all compile run clean
.PHONY: all compile run clean