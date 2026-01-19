/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.librarymanagementsystem_ds;

import static com.mycompany.librarymanagementsystem_ds.SearchField.AUTHOR;
import static com.mycompany.librarymanagementsystem_ds.SearchField.ISBN;
import static com.mycompany.librarymanagementsystem_ds.SearchField.TITLE;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum SortField {
    TITLE, AUTHOR, YEAR
}

enum SearchField {
    TITLE, AUTHOR, ISBN
}

class Book {
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private String genre;
    private BorrowingHistory borrowingHistory = new BorrowingHistory();

    public Book(String title, String author, String isbn, int publicationYear, String genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.genre = genre;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public BorrowingHistory getBorrowingHistory() { return borrowingHistory; }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publicationYear=" + publicationYear +
                ", genre='" + genre + '\'' +
                '}';
    }
}

class BorrowingHistory {
    private class Node {
        String borrower;
        Node next;

        Node(String borrower) {
            this.borrower = borrower;
            this.next = null;
        }
    }

    private Node head;

    public void addBorrower(String borrower) {
        Node newNode = new Node(borrower);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public List<String> getBorrowers() {
        List<String> borrowers = new ArrayList<>();
        Node current = head;
        while (current != null) {
            borrowers.add(current.borrower);
            current = current.next;
        }
        return borrowers;
    }
}

class ActivityStack {
    private List<String> stack = new ArrayList<>();

    public void push(String activity) {
        stack.add(activity);
    }

    public String pop() {
        if (!stack.isEmpty()) {
            return stack.remove(stack.size() - 1);
        }
        return null;
    }

    public String peek() {
        if (!stack.isEmpty()) {
            return stack.get(stack.size() - 1);
        }
        return null;
    }

    // Added method to view all activities without modifying the stack
    public List<String> getAllActivities() {
        List<String> activities = new ArrayList<>();
        for (int i = stack.size() - 1; i >= 0; i--) {
            activities.add(stack.get(i));
        }
        return activities;
    }
}

class Library {
    private Book[] books;
    private int capacity;
    private int count;
    private ActivityStack activityStack = new ActivityStack();

    public Library(int capacity) {
        this.capacity = capacity;
        books = new Book[capacity];
        count = 0;
    }

    public void addBook(Book book) {
        if (count < capacity) {
            books[count++] = book;
            activityStack.push("Added book: " + book.getTitle());
        } else {
            System.out.println("Library is full.");
        }
    }

    public void removeBook(String isbn) {
        for (int i = 0; i < count; i++) {
            if (books[i].getIsbn().equals(isbn)) {
                activityStack.push("Removed book: " + books[i].getTitle());
                for (int j = i; j < count - 1; j++) {
                    books[j] = books[j + 1];
                }
                books[--count] = null; // Clear last slot
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void updateBook(String isbn, String newTitle, String newAuthor, Integer newYear, String newGenre) {
        Book book = linearSearch(isbn, SearchField.ISBN);
        if (book != null) {
            if (newTitle != null && !newTitle.isEmpty()) book.setTitle(newTitle);
            if (newAuthor != null && !newAuthor.isEmpty()) book.setAuthor(newAuthor);
            if (newYear != null) book.setPublicationYear(newYear);
            if (newGenre != null && !newGenre.isEmpty()) book.setGenre(newGenre);
            activityStack.push("Updated book: " + book.getTitle());
        } else {
            System.out.println("Book not found.");
        }
    }

    public Book linearSearch(String query, SearchField field) {
        for (int i = 0; i < count; i++) {
            switch (field) {
                case TITLE:
                    if (books[i].getTitle().equalsIgnoreCase(query)) return books[i];
                case AUTHOR:
                    if (books[i].getAuthor().equalsIgnoreCase(query)) return books[i];
                case ISBN:
                    if (books[i].getIsbn().equals(query)) return books[i];
            }
        }
        return null;
    }

    public Book binarySearch(String query, SearchField field) {
        // Assumes the array is sorted by the specified field
        int low = 0;
        int high = count - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = 0;
            switch (field) {
                case TITLE:
                    cmp = books[mid].getTitle().compareToIgnoreCase(query);
                    break;
                case AUTHOR:
                    cmp = books[mid].getAuthor().compareToIgnoreCase(query);
                    break;
                case ISBN:
                    cmp = books[mid].getIsbn().compareTo(query);
                    break;
            }
            if (cmp == 0) return books[mid];
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    public void bubbleSort(SortField field) {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (compare(books[j], books[j + 1], field) > 0) {
                    swap(j, j + 1);
                }
            }
        }
        activityStack.push("Sorted books using Bubble Sort by " + field);
    }

    public void selectionSort(SortField field) {
        for (int i = 0; i < count - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < count; j++) {
                if (compare(books[j], books[minIdx], field) < 0) {
                    minIdx = j;
                }
            }
            swap(i, minIdx);
        }
        activityStack.push("Sorted books using Selection Sort by " + field);
    }

    public void quickSort(SortField field) {
        quickSort(0, count - 1, field);
        activityStack.push("Sorted books using Quick Sort by " + field);
    }

    private void quickSort(int low, int high, SortField field) {
        if (low < high) {
            int pi = partition(low, high, field);
            quickSort(low, pi - 1, field);
            quickSort(pi + 1, high, field);
        }
    }

    private int partition(int low, int high, SortField field) {
        Book pivot = books[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(books[j], pivot, field) < 0) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    private int compare(Book a, Book b, SortField field) {
        switch (field) {
            case TITLE: return a.getTitle().compareToIgnoreCase(b.getTitle());
            case AUTHOR: return a.getAuthor().compareToIgnoreCase(b.getAuthor());
            case YEAR: return Integer.compare(a.getPublicationYear(), b.getPublicationYear());
            default: return 0;
        }
    }

    private void swap(int i, int j) {
        Book temp = books[i];
        books[i] = books[j];
        books[j] = temp;
    }

    public void borrowBook(String isbn, String borrower) {
        Book book = linearSearch(isbn, SearchField.ISBN);
        if (book != null) {
            book.getBorrowingHistory().addBorrower(borrower);
            activityStack.push("Borrowed book: " + book.getTitle() + " to " + borrower);
        } else {
            System.out.println("Book not found.");
        }
    }

    public List<String> getBorrowingHistory(String isbn) {
        Book book = linearSearch(isbn, SearchField.ISBN);
        if (book != null) {
            return book.getBorrowingHistory().getBorrowers();
        }
        return new ArrayList<>();
    }

    public List<String> getRecentActivities() {
        return activityStack.getAllActivities();
    }

    public void printAllBooks() {
        for (int i = 0; i < count; i++) {
            System.out.println(books[i]);
        }
    }
}
/**
 *
 * @author HP
 */
public class LibraryManagementSystem_DS {

    public static void main(String[] args) {
       Library library = new Library(100);
        Scanner scanner = new Scanner(System.in);

        // Sample dataset for testing
        library.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1925, "Fiction"));
        library.addBook(new Book("1984", "George Orwell", "9780451524935", 1949, "Dystopian"));
        library.addBook(new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084", 1960, "Fiction"));
        library.addBook(new Book("Pride and Prejudice", "Jane Austen", "9780679783268", 1813, "Romance"));

        System.out.println("Sample books added for testing.");

        while (true) {
            System.out.println("\nLibrary Management System Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Update Book");
            System.out.println("4. Linear Search");
            System.out.println("5. Binary Search (requires sorting first)");
            System.out.println("6. Sort Books");
            System.out.println("7. Borrow Book");
            System.out.println("8. View Borrowing History");
            System.out.println("9. View Recent Activities");
            System.out.println("10. View All Books");
            System.out.println("11. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    System.out.print("Enter publication year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter genre: ");
                    String genre = scanner.nextLine();
                    library.addBook(new Book(title, author, isbn, year, genre));
                    break;
                case 2:
                    System.out.print("Enter ISBN to remove: ");
                    isbn = scanner.nextLine();
                    library.removeBook(isbn);
                    break;
                case 3:
                    System.out.print("Enter ISBN to update: ");
                    isbn = scanner.nextLine();
                    System.out.print("Enter new title (leave blank to skip): ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter new author (leave blank to skip): ");
                    String newAuthor = scanner.nextLine();
                    System.out.print("Enter new publication year (0 to skip): ");
                    int newYearInput = scanner.nextInt();
                    scanner.nextLine();
                    Integer newYear = (newYearInput != 0) ? newYearInput : null;
                    System.out.print("Enter new genre (leave blank to skip): ");
                    String newGenre = scanner.nextLine();
                    library.updateBook(isbn, newTitle, newAuthor, newYear, newGenre);
                    break;
                case 4:
                    System.out.println("Search by: 1. Title 2. Author 3. ISBN");
                    int searchBy = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter query: ");
                    String query = scanner.nextLine();
                    SearchField searchField = getSearchField(searchBy);
                    if (searchField != null) {
                        Book found = library.linearSearch(query, searchField);
                        System.out.println(found != null ? "Found: " + found : "Not found.");
                    }
                    break;
                case 5:
                    System.out.println("Binary search requires sorting by the search field first.");
                    System.out.println("Sort and search by: 1. Title 2. Author 3. ISBN");
                    searchBy = scanner.nextInt();
                    scanner.nextLine();
                    searchField = getSearchField(searchBy);
                    if (searchField != null) {
                        SortField sortField = getSortFieldFromSearch(searchField);
                        if (sortField != null) {
                            library.quickSort(sortField); // Using quicksort for efficiency
                            System.out.print("Enter query: ");
                            query = scanner.nextLine();
                            Book found = library.binarySearch(query, searchField);
                            System.out.println(found != null ? "Found: " + found : "Not found.");
                        }
                    }
                    break;
                case 6:
                    System.out.println("Sort by: 1. Title 2. Author 3. Year");
                    int sortBy = scanner.nextInt();
                    scanner.nextLine();
                    SortField sortField = getSortField(sortBy);
                    if (sortField != null) {
                        System.out.println("Algorithm: 1. Bubble Sort 2. Selection Sort 3. Quick Sort");
                        int alg = scanner.nextInt();
                        scanner.nextLine();
                        switch (alg) {
                            case 1: library.bubbleSort(sortField); break;
                            case 2: library.selectionSort(sortField); break;
                            case 3: library.quickSort(sortField); break;
                            default: System.out.println("Invalid algorithm.");
                        }
                        System.out.println("Books sorted.");
                    }
                    break;
                case 7:
                    System.out.print("Enter ISBN to borrow: ");
                    isbn = scanner.nextLine();
                    System.out.print("Enter borrower name: ");
                    String borrower = scanner.nextLine();
                    library.borrowBook(isbn, borrower);
                    break;
                case 8:
                    System.out.print("Enter ISBN to view history: ");
                    isbn = scanner.nextLine();
                    List<String> history = library.getBorrowingHistory(isbn);
                    System.out.println("Borrowing History: " + history);
                    break;
                case 9:
                    List<String> activities = library.getRecentActivities();
                    System.out.println("Recent Activities (latest first):");
                    for (String activity : activities) {
                        System.out.println(activity);
                    }
                    break;
                case 10:
                    library.printAllBooks();
                    break;
                case 11:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static SearchField getSearchField(int searchBy) {
        switch (searchBy) {
            case 1: return SearchField.TITLE;
            case 2: return SearchField.AUTHOR;
            case 3: return SearchField.ISBN;
            default: System.out.println("Invalid search field."); return null;
        }
    }

    private static SortField getSortField(int sortBy) {
        switch (sortBy) {
            case 1: return SortField.TITLE;
            case 2: return SortField.AUTHOR;
            case 3: return SortField.YEAR;
            default: System.out.println("Invalid sort field."); return null;
        }
    }

    private static SortField getSortFieldFromSearch(SearchField searchField) {
        switch (searchField) {
            case TITLE: return SortField.TITLE;
            case AUTHOR: return SortField.AUTHOR;
            case ISBN: return null; // ISBN sorting would require treating as string, but for simplicity, assume TITLE for demo; adjust if needed
            default: return null;
        }
    }
}
