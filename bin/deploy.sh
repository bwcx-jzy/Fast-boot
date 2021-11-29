export GPG_TTY=$(tty)

mvn clean install -DskipTests=true deploy -Prelease