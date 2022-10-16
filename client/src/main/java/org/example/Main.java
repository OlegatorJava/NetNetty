package org.example;

import org.example.model.Command;
import org.example.model.Message;
import org.example.network.Client;
import org.example.network.CommandContainer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static CommandContainer container = new CommandContainer();

    public static void main(String[] args) throws InterruptedException {
       while (true) {
           System.out.println("Введите логин и пароль");
           if (container.auth(scanner.nextLine(), scanner.nextLine())) {
               while (true) {
                   System.out.println("Введите команду(PUT,GET,DELETE), затем имя пользователя, имя файла с расширением");
                   container.execute(scanner.nextLine(), scanner.nextLine(), scanner.nextLine());
                   Thread.sleep(1000);
               }
           }else {
               System.out.println("Неверный логин или пароль, побробуйте еще раз");
           }
       }
    }






}