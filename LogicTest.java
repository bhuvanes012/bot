import java.awt.Dialog;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import enums.OrderStatus;
import interfaces.BitmartService;
import model.AlgoDataModel;
import model.BackTestReport;
import model.KlineData;
import model.OrderDetails;
import model.Symbol; 
import service.BitmartServiceImp;
import util.JsonCreationAndUpdate;
import util.TradeUtil;

public class LogicTest {
	public static String LongFile = "./Long.json";
	String USDT = "USDT";
	static TradeUtil utils = new TradeUtil();
	static String symbol = "GALA_USDT";
	static double highPrice = 0;
	static double totalQuantity = 100000;
	static double totalAmount = 0;

	public static List<Symbol> exchangeSymbolInfo = new ArrayList<Symbol>();
	static List<BackTestReport> report = new ArrayList<BackTestReport>();

	public static void main(String[] args) {

		BitmartService service = new BitmartServiceImp();
		// create the connect
		service.intilizeConnection();
		exchangeSymbolInfo = service.getExchangeSymbolInfo();
		// load the existing data if have
		AlgoDataModel algoModel = new AlgoDataModel();
		algoModel = JsonCreationAndUpdate.loadAlgoDataFromFile(LongFile, algoModel);

		// String orderId = service.sellLimitOrder(symbol, 7000, 0.01);

		// service.getOpenOrderDetails(symbol,"942154914887642112");
		// service.cancelOrder(symbol, "942154914887642112");
		// 942154914887642112 yyyy-MM-dd HH:mm:ss

//	List<KlineData> klineHistory = service.fetchHistoricalData(symbol, "2023-07-12 21:01:01",
//				"2025-02-13 22:01:01");
//		
//		writeKlineDataToFile(klineHistory, "GALA.json");
//		klineHistory.forEach(System.out::println);
		
		int size = (100 - 40 + 1) + (200 - 40 + 1) + (200 - 70 + 1); // Total elements count
        double[] arr = new double[size];

        int index = 0;

        // Fill array with values from 100 to 40 (descending)
        for (int i = 100; i >= 40; i--) {
            arr[index++] = i;
        }

        // Fill array with values from 40 to 200 (ascending)
        for (int i = 40; i <= 200; i++) {
            arr[index++] = i;
        }

        // Fill array with values from 200 to 100 (descending)
        for (int i = 200; i >= 70; i--) {
            arr[index++] = i;
        }

        // Print the array
        System.out.println(Arrays.toString(arr));
		
		List<KlineData> klineHistory =readKlineDataFromFile("GALA.json");
		 for(KlineData data  : klineHistory) {
			
//        for(double price : arr) {
			//utils.sleep(300);

//			SymbolDetails res = service.getLatestPrice(symbol);
//			double latestPrice = price;
			double latestPrice =  Double.parseDouble( data.getOpen());
//			System.out.println(latestPrice);

			if (algoModel.getCurrentHighPrice() == 0) {
//				if(highPrice < latestPrice) {
//					highPrice =latestPrice;
//				}
				algoModel.setCurrentHighPrice(latestPrice);
				// double balance = service.getAccountCoinBalance(symbol);
				double balance = totalQuantity;
				algoModel.setQuantity(balance);
				placeAllOrder(algoModel, service);
			} else {
				double percentageDrop = utils.getPercentage(latestPrice, algoModel.getCurrentHighPrice());
				//int DCAPrec = (highPrice==algoModel.getCurrentHighPrice())?-70:-40;
				int DCAPrec =-80;
				if (percentageDrop < DCAPrec) {
						totalQuantity += totalAmount/latestPrice;
						totalAmount =0;
						BackTestReport reportData = new BackTestReport();
						BigDecimal cuPrice = utils.getPricePrecision(symbol, latestPrice, exchangeSymbolInfo);
						reportData.setPrice(cuPrice.toPlainString());
						reportData.setCurrentBalance(totalAmount);
						reportData.setQuantity(totalQuantity);
						reportData.setComment(" Reached "+DCAPrec+" % fall");
						reportData.setProfit(totalAmount+(totalQuantity*latestPrice));
						
						report.add(reportData);
						System.out.println("******************  Reached "+DCAPrec+" % fall ********* total quantity :"+totalQuantity);
						algoModel = new AlgoDataModel();
						continue;
						
				}
				
				 

				if (percentageDrop >= 10) {
//					if(highPrice < latestPrice) {
//						highPrice =latestPrice;
//					}
					
					if (algoModel.getCurrentHighPrice() < latestPrice) {
						algoModel.setCurrentHighPrice(latestPrice);
						// double balance = service.getAccountCoinBalance(symbol);
						double balance = totalQuantity;
						algoModel.setQuantity(balance);
						placeAllOrder(algoModel, service); // Reorder with new high price
						BackTestReport reportData = new BackTestReport();
						BigDecimal cuPrice = utils.getPricePrecision(symbol, latestPrice, exchangeSymbolInfo);
						reportData.setPrice(cuPrice.toPlainString());
						reportData.setCurrentBalance(totalAmount);
						reportData.setQuantity(totalQuantity);
						reportData.setComment(" 10% up");
						reportData.setProfit(totalAmount+(totalQuantity*latestPrice));
						
						report.add(reportData);
						System.out.println( "10% up :" +latestPrice +"  Amount : "+ totalAmount +" quantity "+ totalQuantity );
					}
				} else {

					for (int i = 0; i < algoModel.getOrderDetails().size(); i++) {
						
						double headPrice = Double.parseDouble(algoModel.getOrderDetails().get(i).getPrice());
						double headQuantity = algoModel.getOrderDetails().get(i).getQuantity();
						OrderStatus headStatus = algoModel.getOrderDetails().get(i).getOrderStatus();
						if (headStatus == OrderStatus.SELL && headPrice >= latestPrice) {
							// sell the quantity
							// once sell complited
							// get average sell price

							double maxBuy = TradeUtil.getPrecetageAmount(headPrice, 0.25);
							double maxPrice = headPrice - maxBuy;
//							if (maxPrice <= latestPrice) {
								if (true) {
									
									double sellFee = TradeUtil.getPrecetageAmount(headQuantity, 0.25);
									 
									double sellAmount = sellFee*headPrice;
									totalAmount += (headPrice* headQuantity)-sellAmount;
									
									totalQuantity -= headQuantity;

									int reEntryPrecentage = (i == 0) ? 10 :8;
									double newPrice = headPrice + ((headPrice * reEntryPrecentage) / 100);
									algoModel.getOrderDetails().get(i).setOrderStatus(OrderStatus.BUY);
									algoModel.getOrderDetails().get(i).setPrice(utils
											.getPricePrecision(symbol, newPrice, exchangeSymbolInfo).toPlainString());
									// amount = avg sell price * quantity

									// quantity = amount/new price

									// will set quantity
									BackTestReport reportData = new BackTestReport();
									BigDecimal cuPrice = utils.getPricePrecision(symbol, latestPrice, exchangeSymbolInfo);
									reportData.setPrice(cuPrice.toPlainString());
									reportData.setCurrentBalance(totalAmount);
									reportData.setQuantity(totalQuantity);
									reportData.setComment("sell Price :");
									reportData.setProfit(totalAmount+(totalQuantity*latestPrice));
									
									report.add(reportData);
									for (OrderDetails details : algoModel.getOrderDetails()) {
										System.out.println(details.toString());
									}
									System.out.println( "sell Price :" +latestPrice +"  Amount : "+ totalAmount +" quantity "+ totalQuantity +"loss :" + ((totalAmount+(totalQuantity*latestPrice)) - (100000*latestPrice)));
									for (OrderDetails details : algoModel.getOrderDetails()) {
										//System.out.println(details.toString());
									}
//								}
							} 
						 

						} else if (headStatus == OrderStatus.BUY && headPrice <= latestPrice) {

							// buy quantity
							// once buy done set sell
							// get average buy price

							double maxBuy = TradeUtil.getPrecetageAmount(headPrice, 0.25);
							double maxPrice = headPrice + maxBuy;
//							if (maxPrice >= latestPrice) {
								if (true) {
									
									
									double QuantityFee = TradeUtil.getPrecetageAmount(headQuantity, 0.25);
									 
									 
									if(headPrice*headQuantity > totalAmount) {
										headQuantity = totalAmount/headPrice;
									}
									totalAmount -= headPrice*headQuantity;
									totalQuantity += (headQuantity-QuantityFee);
									
									int reEntryPrecentage = (i == 0) ? 10 : 8;
									double newPrice = headPrice - ((headPrice * reEntryPrecentage) / 100);
									algoModel.getOrderDetails().get(i).setOrderStatus(OrderStatus.SELL);
									algoModel.getOrderDetails().get(i).setPrice(utils
											.getPricePrecision(symbol, newPrice, exchangeSymbolInfo).toPlainString());
									// amount = avg sell price * quantity

									// quantity = amount/new price

									// will set quantity
									if (i != 0) {
										algoModel.getOrderDetails().get(i - 1)
												.setPrice(utils.getPricePrecision(symbol, newPrice, exchangeSymbolInfo)
														.toPlainString());
										algoModel.getOrderDetails().get(i-1).setOrderStatus(OrderStatus.SELL);
									}else {
										algoModel.getOrderDetails().get(i)
										.setPrice(utils.getPricePrecision(symbol, newPrice, exchangeSymbolInfo)
												.toPlainString());
										algoModel.getOrderDetails().get(i).setOrderStatus(OrderStatus.SELL);
									}
									BackTestReport reportData = new BackTestReport();
									BigDecimal cuPrice = utils.getPricePrecision(symbol, latestPrice, exchangeSymbolInfo);
									reportData.setPrice(cuPrice.toPlainString());
									reportData.setCurrentBalance(totalAmount);
									reportData.setQuantity(totalQuantity);
									reportData.setComment("buy Price :"+ "--> "+headQuantity);
									reportData.setProfit(totalAmount+(totalQuantity*latestPrice));
									
									report.add(reportData);
									
									for (OrderDetails details : algoModel.getOrderDetails()) {
										System.out.println(details.toString());
									}
									System.out.println( "buy Price :" +latestPrice +"  Amount : "+ totalAmount +" quantity "+ totalQuantity +"loss: " + ((totalAmount+(totalQuantity*latestPrice)) - (100000*latestPrice)));
								}
						  }
//						}

					}
					

				}
			}
			// break;
			
			
		}
        
        BackTestReport data = new BackTestReport();
		 
		data.setCurrentBalance(totalAmount);
		data.setQuantity(totalQuantity);
		data.setComment("final"); 
		
		report.add(data);
		
		
		System.out.println(report.toString());
		
		 System.out.println( "Final report :" +"  Amount : "+ totalAmount +" quantity "+ totalQuantity);

	}

	private static void placeAllOrder(AlgoDataModel algoModel, BitmartService service) {
		int precentageForSell = 8;

		double quantity = (algoModel.getQuantity() * precentageForSell) / 100;

		// Initialize orders if none exist
		Double highPrice = algoModel.getCurrentHighPrice();
		double sellPriceInterval = (highPrice * precentageForSell) / 100;

		ArrayList<OrderDetails> allOrder = new ArrayList<>();
		if (algoModel.getCurrentHighPrice() != 0 && algoModel.getOrderDetails().size() == 0) {
			System.out.println("**********re entry after 50 % fall ********"+algoModel.getCurrentHighPrice());
			for (int i = 0; i < 100 / precentageForSell; i++) {
				double finalPrice = highPrice - (sellPriceInterval * (i + 1));
				BigDecimal currentPrice = utils.getPricePrecision(symbol, finalPrice, exchangeSymbolInfo);
				BigDecimal quantitySize = utils.getQuantityPrecision(symbol, quantity, exchangeSymbolInfo);
				OrderDetails detail = new OrderDetails();
				detail.setOrderStatus(OrderStatus.SELL);
				detail.setPrice(currentPrice.toPlainString());
				detail.setQuantity(quantitySize.doubleValue());
				allOrder.add(detail);
				//System.out.println(currentPrice.toPlainString());

				double maxBuy = TradeUtil.getPrecetageAmount(currentPrice.doubleValue(), 0.25);
				double maxPrice = currentPrice.doubleValue() + maxBuy;
				BigDecimal cuPrice = utils.getPricePrecision(symbol, maxPrice, exchangeSymbolInfo);
				//System.out.println("max -> " + cuPrice.toPlainString());
			}
			algoModel.setOrderDetails(allOrder);
			for (OrderDetails details : allOrder) {
				//System.out.println(details.toString());
			}
		} else {
			
			System.out.println("********** 10% up ********->"+algoModel.getCurrentHighPrice());
			for (int i = 0; i < algoModel.getOrderDetails().size(); i++) {

				double finalPrice = highPrice - (sellPriceInterval * (i + 1));
				BigDecimal currentPrice = utils.getPricePrecision(symbol, finalPrice, exchangeSymbolInfo);
				BigDecimal quantitySize = utils.getQuantityPrecision(symbol, quantity, exchangeSymbolInfo);

				algoModel.getOrderDetails().get(i).setOrderStatus(OrderStatus.SELL);
				algoModel.getOrderDetails().get(i).setPrice(currentPrice.toPlainString());
				algoModel.getOrderDetails().get(i).setQuantity(quantitySize.doubleValue());

				//System.out.println(quantitySize.toString());

			}
		}

	}
	
	 public static void writeKlineDataToFile(List<KlineData> klineHistory, String filePath) {
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        try (FileWriter writer = new FileWriter(filePath)) {
	            gson.toJson(klineHistory, writer);
	            System.out.println("Kline data saved to: " + filePath);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 public static List<KlineData> readKlineDataFromFile(String filePath) {
	        Gson gson = new Gson();
	        try (FileReader reader = new FileReader(filePath)) {
	            Type listType = new TypeToken<List<KlineData>>() {}.getType();
	            return gson.fromJson(reader, listType);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

}
