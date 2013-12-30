///////////////////////// single table query
select company_id, company_name, company_desc
from company;

select *
from company
where company_id = 'N';

select product_id, product_name, company_id 
from products;

select *
from products
where company_id = 'N';

select  product_id, quantity_sale, benefit, date_sale 
from sales;

select  *
from sales
where product_id = 'product_N_1';

select  *
from sales
where date_sale = '2008-12-10';

select  *
 from sales 
order by date_sale, quantity_sale;

select  product_id, quantity_sale, benefit, date_sale 
from sales
where product_id like '%A%';

select  product_id, quantity_sale, benefit, date_sale 
from sales
where quantity_sale < 100;

///////////////////////// join query
select  products.product_name, sales.quantity_sale, sales.date_sale, sales.benefit 
 from products, sales 
 where products.product_id = sales.product_id;

select  products.product_name, sales.quantity_sale, sales.date_sale 
 from products, sales 
 where products.product_id = sales.product_id
order by sales.quantity_sale;

select  products.product_name, sales.quantity_sale, sales.date_sale 
 from products, sales 
 where products.product_id = sales.product_id
order by date_sale, quantity_sale;

select  * 
 from products, sales 
 where products.product_id = sales.product_id;

select  products.product_name, sales.quantity_sale, sales.date_sale, sales.benefit  
 from products, sales 
 where products.product_id = sales.product_id 
            and sales.quantity_sale >= 200;

///////////////////////// aggregate
select  product_id, sum(quantity_sale), sum(benefit)
from sales 
group by product_id;

select  product_id, sum(quantity_sale), date_sale
from sales 
group by product_id, date_sale;

select  company_name, count(product_id)
from company, products
where company.company_id = products.company_id
group by company_name;

select  company.company_id, count(product_id)
from company, products
where company.company_id = products.company_id
group by company.company_id;

select count(company_id)
from company;

select count(product_id)
from products;

select  products.product_name, sum(quantity_sale)
 from products, sales 
 where products.product_id = sales.product_id
 group by products.product_name;

select  products.product_name, sum(quantity_sale)
 from products, sales 
 where products.product_id = sales.product_id and date_sale > '2009-01-01'
 group by products.product_name;

select  products.product_name, sum(quantity_sale), date_sale
 from products, sales 
 where products.product_id = sales.product_id and date_sale = '2009-01-03'
 group by products.product_name, date_sale;

select  company.company_id, sum(quantity_sale)
 from company, products, sales 
 where products.product_id = sales.product_id and company.company_id = products.company_id 
group by company.company_id;