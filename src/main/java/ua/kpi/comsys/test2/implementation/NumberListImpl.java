/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import ua.kpi.comsys.test2.NumberList;

/**
 *Лінійний двонаправлений список, десяткова система, додаткова шістнадцяткова,залишок від ділення двох чисел.
 *
 * @author Савенко Анастасія Денисівна
 * Група: ІС-31
 * Варіант: 18
 *
 */

public class NumberListImpl implements NumberList {

    // Внутрішній клас для вузла двонаправленого списку
    private class Node {
        Byte data;
        Node next;
        Node prev;

        Node(Byte data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head; // перший елемент
    private Node tail; // останній елемент
    private int size;  // розмір списку
    private int base;  // система числення (10 для десяткової, 16 для шістнадцяткової)

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.base = 10; // десяткова
    }

    /**
     * Constructor with specified base
     */
    private NumberListImpl(int base) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.base = base;
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                parseDecimalString(line.trim());
            }
        } catch (IOException e) {
            // Якщо файл не існує або є помилка читання, залишаємо список порожнім
        } catch (IllegalArgumentException e) {
            // Якщо дані у файлі некоректні, залишаємо список порожнім
        }
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        if (value != null && !value.trim().isEmpty()) {
            try {
                parseDecimalString(value.trim());
            } catch (IllegalArgumentException e) {
                // Якщо рядок містить некоректні символи, очищаємо список
                clear();
            }
        }
    }

    // Допоміжний метод для парсингу десяткового числа з рядка
    private void parseDecimalString(String value) {
        // Видаляємо всі пробіли і нулі на початку
        value = value.replaceAll("\\s+", "");
        value = value.replaceAll("^0+", "");

        if (value.isEmpty()) {
            value = "0";
        }

        // Додаємо кожну цифру до списку
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') {
                addDigit((byte) (c - '0'));
            } else {
                throw new IllegalArgumentException("Некоректний символ у числі: " + c);
            }
        }
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(toDecimalString());
        } catch (IOException e) {
            throw new RuntimeException("Помилка запису у файл", e);
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 4118; // Номер залікової книжки 18 у форматі 
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        // Переведення з десяткової в шістнадцяткову систему
        NumberListImpl result = new NumberListImpl(16); // створюємо список з base=16

        // Якщо список порожній, повертаємо порожній результат
        if (isEmpty()) {
            return result;
        }

        // Спочатку переводимо список у число
        String decimalStr = toDecimalString();
        if (decimalStr.equals("0")) {
            result.addDigit((byte) 0); // використовуємо внутрішній метод
            return result;
        }

        // Використовуємо BigInteger для переведення великих чисел
        java.math.BigInteger decimal = new java.math.BigInteger(decimalStr);
        java.math.BigInteger baseValue = java.math.BigInteger.valueOf(16);

        // Переводимо в шістнадцяткову систему
        while (decimal.compareTo(java.math.BigInteger.ZERO) > 0) {
            java.math.BigInteger remainder = decimal.mod(baseValue);
            result.addDigit(0, remainder.byteValue()); // додаємо на початок
            decimal = decimal.divide(baseValue);
        }

        return result;
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        // Операція: множення двох чисел (this * arg)
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException("Аргумент не може бути порожнім");
        }

        String multiplicand = this.toDecimalString();
        String multiplier = convertToDecimalString(arg);

        // Використовуємо BigInteger для множення
        java.math.BigInteger a = new java.math.BigInteger(multiplicand);
        java.math.BigInteger b = new java.math.BigInteger(multiplier);
        java.math.BigInteger product = a.multiply(b);

        return new NumberListImpl(product.toString());
    }

    // Допоміжний метод для конвертації NumberList в десятковий рядок
    private String convertToDecimalString(NumberList list) {
        if (list.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (Byte digit : list) {
            sb.append(digit);
        }
        return sb.toString();
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) {
            return "0";
        }

        // Якщо список вже в десятковій системі, просто повертаємо як є
        if (base == 10) {
            StringBuilder sb = new StringBuilder();
            Node current = head;
            while (current != null) {
                sb.append(current.data);
                current = current.next;
            }
            return sb.toString();
        }

        // Інакше конвертуємо з поточної системи числення в десяткову
        java.math.BigInteger result = java.math.BigInteger.ZERO;
        java.math.BigInteger baseValue = java.math.BigInteger.valueOf(base);

        Node current = head;
        while (current != null) {
            result = result.multiply(baseValue);
            result = result.add(java.math.BigInteger.valueOf(current.data));
            current = current.next;
        }

        return result.toString();
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            // Якщо система шістнадцяткова, конвертуємо 10-15 в A-F
            if (base == 16 && current.data >= 10) {
                sb.append((char)('A' + (current.data - 10)));
            } else {
                sb.append(current.data);
            }
            current = current.next;
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof NumberList)) return false;

        NumberList other = (NumberList) o;
        if (this.size() != other.size()) return false;

        Iterator<Byte> thisIter = this.iterator();
        Iterator<Byte> otherIter = other.iterator();

        while (thisIter.hasNext() && otherIter.hasNext()) {
            Byte thisByte = thisIter.next();
            Byte otherByte = otherIter.next();
            if (!thisByte.equals(otherByte)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        Node current = head;
        while (current != null) {
            if (current.data.equals(o)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Byte data = current.data;
                current = current.next;
                return data;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int index = 0;
        Node current = head;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }


    // Внутрішній метод для додавання цифри без перевірки діапазону
    private boolean addDigit(Byte e) {
        if (e == null) {
            throw new NullPointerException("Null elements not permitted");
        }

        Node newNode = new Node(e);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        return true;
    }

    // Внутрішній метод для додавання цифри на вказану позицію без перевірки діапазону
    private void addDigit(int index, Byte element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (element == null) {
            throw new NullPointerException("Null elements not permitted");
        }

        if (index == size) {
            addDigit(element);
            return;
        }

        Node newNode = new Node(element);
        if (index == 0) {
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            Node node = getNode(index);
            newNode.next = node;
            newNode.prev = node.prev;
            node.prev.next = newNode;
            node.prev = newNode;
        }
        size++;
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("Null elements not permitted");
        }
        // Перевірка що це дійсна цифра для поточної системи числення
        if (e < 0 || e >= base) {
            throw new IllegalArgumentException("Digit must be between 0 and " + (base - 1));
        }
        return addDigit(e);
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        Node current = head;
        while (current != null) {
            if (current.data.equals(o)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Допоміжний метод для видалення вузла
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        size--;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        boolean modified = false;
        for (Byte e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            if (c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            if (!c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        }
        return modified;
    }


    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }


    @Override
    public Byte get(int index) {
        return getNode(index).data;
    }


    @Override
    public Byte set(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("Null elements not permitted");
        }
        if (element < 0 || element >= base) {
            throw new IllegalArgumentException("Digit must be between 0 and " + (base - 1));
        }
        Node node = getNode(index);
        Byte oldValue = node.data;
        node.data = element;
        return oldValue;
    }


    @Override
    public void add(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("Null elements not permitted");
        }
        if (element < 0 || element >= base) {
            throw new IllegalArgumentException("Digit must be between 0 and " + (base - 1));
        }
        addDigit(index, element);
    }


    @Override
    public Byte remove(int index) {
        Node node = getNode(index);
        Byte oldValue = node.data;
        removeNode(node);
        return oldValue;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        int index = 0;
        Node current = head;
        while (current != null) {
            if (current.data.equals(o)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        int index = size - 1;
        Node current = tail;
        while (current != null) {
            if (current.data.equals(o)) {
                return index;
            }
            current = current.prev;
            index--;
        }
        return -1;
    }

    // Допоміжний метод для отримання вузла за індексом
    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node current;
        // Оптимізація: йдемо з того кінця, який ближче
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return new ListIterator<Byte>() {
            private Node current = (index == size) ? null : getNode(index);
            private Node lastReturned = null;
            private int currentIndex = index;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                lastReturned = (current == null) ? head : current;
                Byte data = lastReturned.data;
                current = lastReturned.next;
                currentIndex++;
                return data;
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex > 0;
            }

            @Override
            public Byte previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                current = (current == null) ? tail : current.prev;
                lastReturned = current;
                currentIndex--;
                return lastReturned.data;
            }

            @Override
            public int nextIndex() {
                return currentIndex;
            }

            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(Byte e) {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                NumberListImpl.this.set(currentIndex - 1, e);
            }

            @Override
            public void add(Byte e) {
                throw new UnsupportedOperationException();
            }
        };
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                "fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", size: " + size);
        }
        NumberListImpl sublist = new NumberListImpl(this.base); // створюємо з такою ж системою числення
        if (fromIndex == toIndex) {
            return sublist; // порожній список
        }
        Node current = getNode(fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            sublist.addDigit(current.data); // використовуємо addDigit
            current = current.next;
        }
        return sublist;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        if (index1 == index2) {
            return true;
        }

        Node node1 = getNode(index1);
        Node node2 = getNode(index2);

        Byte temp = node1.data;
        node1.data = node2.data;
        node2.data = temp;

        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }

        // bubble
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current != null && current.next != null) {
                if (current.data > current.next.data) {
                    Byte temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }


    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }

        // bubble sort
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current != null && current.next != null) {
                if (current.data < current.next.data) {
                    Byte temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) {
            return;
        }

        // Переміщуємо перший елемент в кінець
        Byte firstData = head.data;
        Node current = head;
        while (current.next != null) {
            current.data = current.next.data;
            current = current.next;
        }
        current.data = firstData;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) {
            return;
        }

        // Переміщуємо останній елемент на початок
        Byte lastData = tail.data;
        Node current = tail;
        while (current.prev != null) {
            current.data = current.prev.data;
            current = current.prev;
        }
        current.data = lastData;
    }
}

