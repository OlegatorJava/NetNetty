package org.example.network;

import org.example.model.Command;
import org.example.model.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandContainer {

    private ClientHandler clientHandler = new ClientHandler();
    private boolean authOK;

    public void execute(String command, String nameUser, String file){
        if(command.equals(Command.GET.getCommand())){
            get();
        }else if(command.equals(Command.PUT.getCommand())){
            put(nameUser,file);
        }else if(command.equals(Command.DELETE.getCommand())){
            delete(nameUser,file);
        }
    }
    public boolean auth(String login, String password){
           Message message = Message.builder()
                   .command(Command.AUTH)
                   .nameUser(login)
                   .password(password)
                   .build();
           new Client().send(message,response ->{
               System.out.println("Данные отправлены");
           });
           authOK = clientHandler.isAuthOK();
return authOK;



    }

    private void get() {
        new Thread(() -> {
            Message message = Message.builder()
                    .command(Command.GET)
                    .file("file.txt")
                    .build();
            new Client().send(message, response -> {
                Path file = Path.of("client", response.getFile());
                try {
                    Files.createFile(file);
                } catch (FileAlreadyExistsException e) {

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try (FileOutputStream output = new FileOutputStream(file.toFile())) {
                    output.write(response.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }).start();
    }
    private void put(String nameUser, String fileName) {
        new Thread(() -> {
            Path send = Path.of("client", fileName);
            try {
                Message message = Message.builder()
                        .command(Command.PUT)
                        .nameUser(nameUser)
                        .file(send.getFileName().toString())
                        .length(Files.size(send))
                        .data(Files.readAllBytes(send))
                        .build();
                new Client().send(message, resposne -> {
                    System.out.printf("File %s %s", resposne.getFile(), resposne.getStatus());
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void delete(String nameUser, String nameFile){
        new Thread(() -> {
            Message message = Message.builder()
                    .command(Command.DELETE)
                    .nameUser(nameUser)
                    .file(nameFile)
                    .build();
            new Client().send(message,response -> {
                System.out.printf("File %s %s", response.getFile(), response.getStatus());
            });
        }).start();
    }

}
