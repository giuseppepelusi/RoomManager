SRC_DIR = src
BIN_DIR = bin
DOC_DIR = doc
PACKAGES = app:controllers:models:utils:views

all: compile run

compile:
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRC_DIR)/app/Main.java

run:
	java -cp $(BIN_DIR) app.Main

jar: compile
	jar cmf config/manifest.txt RoomManager.jar -C bin . -C . config/rooms.txt

doc:
	javadoc -d $(DOC_DIR) -sourcepath $(SRC_DIR) -subpackages $(PACKAGES)

clean:
	rm -rf $(BIN_DIR)
	rm -rf $(DOC_DIR)
	rm -f *.jar
	rm -f *.resv

.SILENT: all compile run jar doc clean
.PHONY: all compile run jar doc clean
