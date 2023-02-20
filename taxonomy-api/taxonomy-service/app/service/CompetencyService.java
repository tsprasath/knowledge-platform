package service;

import models.Competency;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import repository.CompetencyRepositoryTrait;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class CompetencyService implements CompetencyRepositoryTrait {

    @Override
    public List<Competency> findAll() {
        return null;
    }

    @Override
    public List<Competency> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Competency> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Competency> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Competency entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Competency> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Competency> S save(S entity) {
        return entity;
    }

    @Override
    public <S extends Competency> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Competency> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Competency> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Competency> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Competency> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Competency getOne(String s) {
        return null;
    }

    @Override
    public Competency getById(String s) {
        return null;
    }

    @Override
    public <S extends Competency> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Competency> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Competency> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Competency> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Competency> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Competency> boolean exists(Example<S> example) {
        return false;
    }
}
