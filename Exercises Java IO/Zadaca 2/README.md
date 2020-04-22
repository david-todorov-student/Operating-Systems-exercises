First partial exam (Group 2) Problem 2 (0 / 0)

Using Java I/O, implement the following methods in the ExamIO class:

    (10 points)void copyLargeTxtFiles(String from, String to, long size)
        Copies all .txt files which are larger than size (in bytes) from the from directory into the to directory. If the from directory does not exist, you should write "Does not exist" and if to does not exist you need to create it.

    (10 points)void serializeData(String destination, List<byte[]> data)
        The list of data in data is written into the destination file, without delimiters (as a continuous stream of bytes). All elements from data have the same length (same number of bytes).

    (Bonus 5 points)byte[] deserializeDataAtPosition(String source, long position, long elementLength)
        Reads and returns the data at the position position from the source file, which contains a large number of data, all with the same length in bytes, without delimiters. All data elements have the same elementLength length. You should not read the entire file in this method.

