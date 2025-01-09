SRC_DIR = src
BIN_DIR = bin
DOC_DIR = doc
PACKAGES = app:controllers:models:utils:views
MAIN_CLASS = app.Main

all: compile run

compile:
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/app/Main.java

run:
	java -cp $(BIN_DIR) $(MAIN_CLASS)

doc:
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) -subpackages $(PACKAGES)

clean:
	rm -rf $(BIN_DIR)
	rm -rf $(DOC_DIR)
	rm -f autosave.resv

.SILENT: all compile run doc clean
.PHONY: all compile run doc clean
