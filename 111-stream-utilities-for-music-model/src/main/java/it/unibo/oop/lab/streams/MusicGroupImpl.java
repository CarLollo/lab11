package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        final Stream<String> res = songs.stream()
            .map(i -> i.getSongName())
            .sorted((o1, o2) -> o1.compareTo(o2));
        return res;
    }

    @Override
    public Stream<String> albumNames() {
        final Stream<String> res = albums.keySet().stream();
        return res;
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final Stream<String> res = albums.entrySet().stream()
            .filter( i -> i.getValue().equals(year))
            .map(i -> i.getKey());
        return res;
    }

    @Override
    public int countSongs(final String albumName) {
        return songs.stream()
            .filter(i -> i.getAlbumName().isPresent())
            .map(i -> i.getAlbumName().get())
            .filter(i -> i.equals(albumName))
            .map(i -> 1)
            .reduce((a, b) -> a+b)
            .get();
    }

    @Override
    public int countSongsInNoAlbum() {
        return songs.stream()
            .filter(i -> i.getAlbumName().isEmpty())
            .map(i -> 1)
            .reduce((a, b) -> a+b)
            .get();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream()
            .filter(i -> i.getAlbumName().isPresent())
            .filter(i -> i.getAlbumName().get().equals(albumName))
            .mapToDouble(i -> i.getDuration())
            .average();
    }

    @Override
    public Optional<String> longestSong() {
        final Optional<String> s = songs.stream()
            .collect(Collectors.maxBy((x, y) -> Double.compare(x.getDuration(), y.getDuration())))
            .map(i -> i.getSongName());
        return s;
    }

    @Override
    public Optional<String> longestAlbum() {
        final Optional<String> o = songs.stream()
            .filter(i -> i.getAlbumName().isPresent())
            .collect(Collectors.groupingBy(i -> i.getAlbumName(), Collectors.summingDouble(i -> i.getDuration())))
            .entrySet().stream()
            .collect(Collectors.maxBy((x, y) -> Double.compare(x.getValue(), y.getValue())))
            //Ottengo un Optional<Optional<String>> e con la map rendo Optional<String> come String e quindi ho Optional<String>
            .map(e -> e.getKey().get());
        return o;
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
