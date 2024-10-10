package info.kgeorgiy.ja.podkorytov.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements AdvancedQuery {
    /*
        AdvancedQuery
     */

    private <T, R> R getMaxFromStream(
            Stream<T> stream,
            ToIntFunction<T> firstCmp,
            Comparator<? super T> secondCmp,
            Function<T, R> transform,
            R defaultValue) {
        return stream.max(
                Comparator.comparingInt(firstCmp)
                        .thenComparing(secondCmp)) // :NOTE: reuse comp
                .map(transform)
                .orElse(defaultValue);
    }

    private GroupName getMaxByCountOr(Stream<Map.Entry<GroupName, Long>> stream,
                                      Comparator<GroupName> cmp) {
        return stream
                .max(Comparator.comparing(Map.Entry<GroupName, Long>::getValue) // Looking for a max value by long
                        .thenComparing(Map.Entry::getKey, cmp)) // If there are many equal values, sorting keys with cmp
                // :NOTE: reuse comp
                .map(Map.Entry::getKey) // turning Map.Entry<GroupName, Long> to GroupName
                .orElse(null); // returning null in case of empty optional
    }

    private <T> List<T> getSubsequence(Collection<Student> students, int[] indexes, Function<Student, T> transform) {
        return Arrays.stream(indexes)
                .mapToObj(idx -> students.stream().toList().get(idx))
                .map(transform)
                .collect(Collectors.toList());
    }

    public String getPopularName(Collection<Student> students, int popular) {
        return getMaxFromStream(
                students.stream()
                        .collect(Collectors.groupingBy(
                                Student::getFirstName,
                                HashMap::new,
                                Collectors.mapping(
                                        Student::getGroup,
                                        Collectors.toSet()
                                )
                        ))
                        .entrySet()
                        .stream(),
                entry -> popular * entry.getValue().size(),
                Map.Entry.<String, Set<GroupName>>comparingByKey().reversed(),
                Map.Entry::getKey,
                ""
        );
    }

    @Override
    public String getMostPopularName(Collection<Student> students) {
        return getPopularName(students, 1);
    }

    @Override
    public String getLeastPopularName(Collection<Student> students) {
        return getPopularName(students, -1);
    }

    @Override
    public List<String> getFirstNames(Collection<Student> students, int[] indices) {
        return getSubsequence(students, indices, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(Collection<Student> students, int[] indices) {
        return getSubsequence(students, indices, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(Collection<Student> students, int[] indices) {
        return getSubsequence(students, indices, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(Collection<Student> students, int[] indices) {
        return indices.length == 0 ? Collections.emptyList() : getSubsequence(students, indices, this::getFullName);
    }

    /*
        GroupQuery
     */

    private Stream<Map.Entry<GroupName, List<Student>>> getGroupsStream(Collection<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(
                        Student::getGroup,
                        TreeMap::new,
                        Collectors.toList()
                ))
                .entrySet()
                .stream();
    }

    private List<Group> getGroupsBy(Collection<Student> students, Function<Collection<Student>, List<Student>> f) {
        return getGroupsStream(students)
                .map(groupNameListEntry -> new Group(
                        groupNameListEntry.getKey(),
                        f.apply(groupNameListEntry.getValue())
                ))
                .toList();
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsByName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsById);
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        return getMaxByCountOr(
                students.stream()
                        .collect(Collectors.groupingBy(Student::getGroup, Collectors.counting()))
                        .entrySet()
                        .stream(),
                Comparator.naturalOrder()
        );
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        return getMaxByCountOr(
                getGroupsStream(students)
                        .map(groupNameListEntry -> Map.entry(
                                groupNameListEntry.getKey(),
                                (long) getDistinctFirstNames(groupNameListEntry.getValue()).size())),
                Comparator.reverseOrder()
        );
    }

    /*
        StudentQuery
     */

    private Stream<String> stringGetter(List<Student> students, Function<Student, String> getter) {
        return students.stream()
                .map(getter);
    }

    private String getFullName(Student st) {
        return st.getFirstName() + " " + st.getLastName();
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return stringGetter(students, Student::getFirstName).toList();
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return stringGetter(students, Student::getLastName).toList();
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return students.stream()
                .map(Student::getGroup)
                .toList();
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return stringGetter(students, this::getFullName).toList();
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Student::compareTo)
                .orElse(
                        new Student(
                                -1,
                                "",
                                "",
                                GroupName.M3239) // :NOTE: Сомнительно
                )
                .getFirstName();
    }

    private List<Student> sortStudentsBy(Collection<Student> students, Comparator<Student> cmp) {
        return students.stream()
                .sorted(cmp)
                .toList();
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudentsBy(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudentsBy(
                students,
                Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName)
                        .thenComparing(Comparator.comparing(Student::getId).reversed()));
    }

    private Stream<Student> findStudentsBy(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream()
                .filter(predicate);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return sortStudentsByName(findStudentsBy(students, s -> s.getFirstName().equals(name)).toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return sortStudentsByName(findStudentsBy(students, s -> s.getLastName().equals(name)).toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return sortStudentsByName(findStudentsBy(students, s -> s.getGroup().equals(group)).toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group)
                .stream()
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)
                ));
    }
}
