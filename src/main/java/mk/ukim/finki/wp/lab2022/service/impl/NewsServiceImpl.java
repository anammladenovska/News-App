package mk.ukim.finki.wp.lab2022.service.impl;


import mk.ukim.finki.wp.lab2022.model.News;
import mk.ukim.finki.wp.lab2022.model.NewsCategory;
import mk.ukim.finki.wp.lab2022.model.NewsType;
import mk.ukim.finki.wp.lab2022.model.exceptions.InvalidNewsIdException;
import mk.ukim.finki.wp.lab2022.repository.NewsCategoryRepository;
import mk.ukim.finki.wp.lab2022.repository.NewsRepository;
import mk.ukim.finki.wp.lab2022.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsServiceImpl(NewsRepository newsRepository, NewsCategoryRepository newsCategoryRepository) {
        this.newsRepository = newsRepository;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @Override
    public List<News> listAllNews() {
        return this.newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
    }

    @Override
    public News create(String name, String description, Double price, NewsType type, Long category) {
        NewsCategory newsCategory = this.newsCategoryRepository.findById(category).orElseThrow(InvalidNewsIdException::new);
        News news = new News(name,description,price,type,newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News update(Long id, String name, String description, Double price, NewsType type, Long category) {
        News news = this.findById(id);
        news.setId(id);
        news.setName(name);
        news.setDescription(description);
        news.setPrice(price);
        news.setType(type);
        NewsCategory newsCategory = this.newsCategoryRepository.findById(category).orElseThrow(InvalidNewsIdException::new);
        news.setCategory(newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News delete(Long id) {
        News news = this.findById(id);
        this.newsRepository.delete(news);
        return news;
    }


    @Override
    public News like(Long id) {
        List<News> allNews = newsRepository.findAll();
        News news = newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
        for(News n: allNews){
            newsRepository.delete(n);
            if(n.getId().equals(id))
                n.setLikes(n.getLikes() + 1);
            newsRepository.save(n);
        }
        return news;
    }

    @Override
    public List<News> listNewsWithPriceLessThanAndType(Double price, NewsType type) {

        if(price!=null && type!=null){
            return this.newsRepository.findAllByPriceLessThanAndType(price, type);
        }
        else if(price!=null){
            return this.newsRepository.findAllByPriceLessThan(price);
        }
        else if(type!=null){
            return this.newsRepository.findAllByType(type);
        }
        else{
            return this.newsRepository.findAll();
        }
    }
}
